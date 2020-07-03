package program;

import command.Command;
import dopFiles.Console;
import dopFiles.Writer;
import exceptions.EndOfFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerWithProperThreads {

    private static final Logger logger = LogManager.getLogger(ServerWithProperThreads.class);
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static Collection collection;
    private static final AtomicBoolean globalKillFlag = new AtomicBoolean(false);

    public static void main(String[] args) {
        collection = Collection.startFromSave(args);
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress("localhost", 4329));
            ssc.configureBlocking(false);

            Writer.writeln("Сервер запущен.");
            logger.info("Сервер запущен. " + ssc.getLocalAddress());

            new Thread(() -> {
                try {
                    while (!Console.console.read().equals("exit")) {
                        Writer.writeln("Команда не найдена. Колтчество потоков: " + Thread.activeCount());
                    }
                    globalKillFlag.set(true);
                } catch (EndOfFileException e) {
                    globalKillFlag.set(true);
                    Writer.writeln("Работа с консолью завершина.");
                }
            }).start();

            while (!globalKillFlag.get()) {
                SocketChannel s = ssc.accept();
                if (s != null) {
                    System.out.println("Соединение с " + s);
                    s.configureBlocking(false);
                    ConcurrentLinkedQueue<ByteBuffer> messages = new ConcurrentLinkedQueue<>();
                    final AtomicBoolean killFlag = new AtomicBoolean(false);
                    //открыть поток для чтения
                    pool.submit(() -> read(s, messages, killFlag));
                    //открыть поток для записи
                    new Thread(() -> answer(s, messages, killFlag)).start();
                }
                Thread.sleep(500);
                //System.out.println(Thread.activeCount());
            }
            ssc.close();
            logger.info("Сервер закрыт");
        } catch (IOException | InterruptedException e) {
            Writer.writeln("Пренудительное закрытие сервера.");
            logger.error("Пренудительное закрытие сервера.");
            logger.error(e.getLocalizedMessage());
        }
        pool.shutdownNow();
        SaveManagement.saveToFile(collection);
        Writer.writeln("Коллекция сохранена");
        logger.info("Коллекция сохранена");
    }

    private static void read(SocketChannel channel, ConcurrentLinkedQueue<ByteBuffer> messages, AtomicBoolean killFlag) {
        try {
            while (true) {
                ByteBuffer buf = ByteBuffer.allocateDirect(8192);
                int read = channel.read(buf);
                if (killFlag.get() || globalKillFlag.get()) {
                    channel.close();
                    break;
                }
                if (read == -1) {
                    channel.close();
                    throw new IOException();
                } else if (read != 0) {
                    //поток обработки
                    Writer.writeln("Обработка пошла");
                    pool.submit(() -> {
                        try {
                            buf.flip();
                            process(buf, messages);
                        } catch (IOException | ClassNotFoundException e) {
                            killFlag.set(true);
                            Writer.writeln("При обработке команты произошли ошибки.");
                            Writer.writeln("Ради безопасности соединение было остановлено.");
                        }
                    });
                }
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            Writer.writeln("Соединение разорвано...");
            Writer.writeln("Сервер продолжит работать. Попробуйте запустить другой клиент, чтобы восстановить соединение.");
            logger.info("Соединение разорвано. Сервер продолжил работать.");
        }
        System.out.println("Закрылся read");
        killFlag.set(true);
    }

    private static void process(ByteBuffer buf, ConcurrentLinkedQueue<ByteBuffer> messages) throws IOException, ClassNotFoundException {
        byte[] arr = new byte[buf.remaining()];
        buf.get(arr);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(arr));
        Command command = (Command) objectInputStream.readObject();
        objectInputStream.close();
        buf.clear();
        Writer w = CommanderServer.switcher(command, collection);

        Writer.writeln("Вызвана команада: " + command.getCurrent().toString());
        logger.info("Вызвана команада: " + command.toString());
        logger.info("Комманда обработана успешно. Ответ:" + (w.toString()));


        int number = 0;
        w.shatter();
        Writer subW;

        do {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            subW = w.getSubWriter((number)*10,(number + 1)*10);
            objectOutputStream.writeObject(subW);
            buf = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            messages.add(buf);
            objectOutputStream.close();
            number++;
        } while (!subW.isEnd());
    }

    private static void answer(SocketChannel s, ConcurrentLinkedQueue<ByteBuffer> messages, AtomicBoolean killFlag) {
        try {
            while (!(killFlag.get() || globalKillFlag.get())) {
                ByteBuffer buf;
                if (!messages.isEmpty()) {
                    Writer.writeln(messages.size());
                    buf = messages.poll();
                    s.write(buf);
                }
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            Writer.writeln("Не удалось отправить все данные.");
        }
        System.out.println("Закрылся answer");
        killFlag.set(true);
    }
}
