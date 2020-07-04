package program;

import command.Command;
import command.Commands;
import dopFiles.AbstractReader;
import dopFiles.User;
import dopFiles.Utils;
import dopFiles.Writer;
import dopFiles.Console;
import exceptions.EndOfFileException;
import exceptions.FailedCheckException;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {

    private static boolean exit = false;
    private static User user = new User("login", "password");

    public static void main(String[] args) {
        try {
            do {
                InetSocketAddress addr;
                try {
                     addr = new InetSocketAddress(args[0], Utils.portCheck.checker(Integer.parseInt(args[1])));
                } catch (NumberFormatException |ArrayIndexOutOfBoundsException | FailedCheckException  e) {
                    addr = new InetSocketAddress(InetAddress.getByName("localhost"), 4329);
                }
                Selector selector = Selector.open();
                SocketChannel sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.connect(addr);
                sc.register(selector, SelectionKey.OP_CONNECT);
                try {
                    while (true) {
                        if (selector.select() > 0) {
                            Boolean doneStatus = process(selector);
                            if (doneStatus) {
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    Writer.writeln("\u001B[31m" + "Не удалось получить или прочитать ответ от сервера." + "\u001B[0m");
                    Writer.writeln("\u001B[31m" + "Соединение разорвано." + "\u001B[0m");
                }
                sc.close();
            } while (!exit && Console.handlerB("Попробовать переподключить клиент? boolean: ", Utils.boolCheck));
        } catch (IOException e) {
            Writer.writeln("\u001B[31m" + "Неправильное закрытие сокета." + "\u001B[0m");
        } catch (ClassNotFoundException e) {
            Writer.writeln("\u001B[31m" + "Отсутствует класс для сериализации." + "\u001B[0m");
            exit = true;
        } catch (EndOfFileException e) {
            Writer.writeln("\u001B[31m" + "Неожиданное завершение работы консоли" + "\u001B[0m");//ctrl-d
            exit = true;
        }
        Writer.writeln("Клиент был закрыт...");
    }

    public static Boolean process(Selector selector) throws IOException, EndOfFileException, ClassNotFoundException {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();

            if (key.isConnectable()) {
                Boolean connected = processConnect(selector, key);
                if (!connected) {
                    return true;
                }
            }
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer bb = ByteBuffer.allocate(8*1024);
                bb.clear();
                sc.read(bb);

                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bb.array()));
                Writer w = (Writer) objectInputStream.readObject();
                if (w != null) {
                    w.writeAll();
                    if (w.isEnd())
                        key.interestOps(SelectionKey.OP_WRITE);
                } else {
                    Writer.writeln("Отсутствуют данные вывода");
                    key.interestOps(SelectionKey.OP_WRITE);
                }
            }
            if (key.isWritable()) {

                Writer.write("\u001B[33m" + "Ожидание ввода команды: " + "\u001B[0m");
                String[] com = AbstractReader.splitter(Console.console.read());
                Command command = CommanderClient.switcher(user, com[0], com[1]);

                if (command == null)
                    return false;
                else if (command.getCurrent() == Commands.EXIT) {
                    exit = true;
                    return true;
                }

                SocketChannel sc = (SocketChannel) key.channel();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(command);
                objectOutputStream.flush();
                //конец
                ByteBuffer bb = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
                if (bb.array().length > 8192)
                {
                    Writer.writeln("Отправляемые дынные слишком большие (" + bb.array().length + " > 8192). ");
                    return false;
                }
                sc.write(bb);
                bb.clear();

                key.interestOps(SelectionKey.OP_READ);
            }
        }
        return false;
    }

    public static Boolean processConnect(Selector selector, SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (sc.isConnectionPending()) {
                sc.finishConnect();
            }
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_WRITE);
            Writer.writeln("Соединение установлено: " + sc.getLocalAddress());

        } catch (IOException e) {
            key.cancel();
            Writer.writeln("Сервер недоступен. Попробуйте переподключиться позже.");
            return false;
        }
        return true;
    }
}