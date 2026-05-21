package com.zsm.aiagentlasted.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 基于文件的聊天内存持久化实现（支持滑动窗口）
 * 使用 Kryo 进行高性能序列化
 *
 * @author zsm
 */
@Slf4j
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;
    private final int maxMessages;  // 最大保留消息数，-1表示不限制
    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        kryo.register(ArrayList.class);
        kryo.register(Message.class);
    }

    /**
     * 构造对象时，指定文件保存目录（不限制消息数量）
     */
    public FileBasedChatMemory(String dir) {
        this(dir, -1);
    }

    /**
     * 构造对象，指定文件保存目录和最大消息数
     * @param dir 保存目录
     * @param maxMessages 最大保留消息数，超过时会截断最旧的消息
     */
    public FileBasedChatMemory(String dir, int maxMessages) {
        this.BASE_DIR = dir;
        this.maxMessages = maxMessages;
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    private File getConversationFile(String conversationId) {
        String safeId = conversationId.replaceAll("[^a-zA-Z0-9_-]", "_");
        return new File(BASE_DIR, safeId + ".kryo");
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (conversationId == null || messages == null || messages.isEmpty()) {
            return;
        }

        List<Message> existingMessages = get(conversationId);
        List<Message> allMessages = new ArrayList<>(existingMessages);
        allMessages.addAll(messages);

        // 如果超过最大限制，截断最旧的消息
        if (maxMessages > 0 && allMessages.size() > maxMessages) {
            allMessages = allMessages.subList(allMessages.size() - maxMessages, allMessages.size());
        }

        File file = getConversationFile(conversationId);
        try (FileOutputStream fos = new FileOutputStream(file);
             Output output = new Output(fos)) {

            kryo.writeObject(output, allMessages);
            output.flush();

        } catch (IOException e) {
            log.error("未能保存聊天内存用于对话：{}", conversationId, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Message> get(String conversationId) {
        if (conversationId == null) {
            return new ArrayList<>();
        }

        File file = getConversationFile(conversationId);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileInputStream fis = new FileInputStream(file);
             Input input = new Input(fis)) {

            List<Message> messages = kryo.readObject(input, ArrayList.class);

            // 如果设置了最大消息数，只返回最近的消息
            if (maxMessages > 0 && messages.size() > maxMessages) {
                return messages.subList(messages.size() - maxMessages, messages.size());
            }
            return messages;

        } catch (IOException e) {
            log.error("未能加载聊天内存用于对话：{}", conversationId, e);
        }
        return Collections.emptyList();
    }

    @Override
    public void clear(String conversationId) {
        if (conversationId == null) {
            return;
        }

        File file = getConversationFile(conversationId);
        if (file.exists()) {
            if (!file.delete()) {
                log.error("未能删除聊天内存文件用于对话：{}", conversationId);
            }
        }
    }

    /**
     * 获取所有对话ID列表
     */
    public List<String> getAllConversationIds() {
        File baseDir = new File(BASE_DIR);
        File[] files = baseDir.listFiles((dir, name) -> name.endsWith(".kryo"));

        List<String> ids = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                ids.add(name.substring(0, name.length() - 5));
            }
        }
        return ids;
    }
}
