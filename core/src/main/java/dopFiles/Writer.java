package dopFiles;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


public class Writer implements Serializable {
    LinkedList<String> toWrite = new LinkedList<>();

    public static void write(Object s) {
        System.out.print(s);
    }

    public static void writeln(Object s) {
        System.out.println(s);
    }

    public void addToList(boolean isLn, Object o) {
        toWrite.add(o.toString() + (isLn ? "\n" : ""));
    }

    public void shatter()
    {
        for (int i = 0; i < toWrite.size(); i++)
        {
            String s = toWrite.get(i);
            if (s.length() > 500)
            {
                toWrite.set(i, s.substring(0,500));
                toWrite.add(i+1, s.substring(500));
            }
        }
    }

    public Writer getSubWriter(int beg, int end)
    {
        Writer w = new Writer();
        w.toWrite = this.toWrite.stream().skip(beg).limit(end-beg).collect(Collectors.toCollection(LinkedList::new));
        return w;
    }

    public boolean isEnd()
    {
        //try {
            return toWrite.getLast().equals("end");
        /*}
        catch (NoSuchElementException e) {
            return true;
        }*/
    }

    public void writeAll() {
        toWrite.forEach(s -> {
            if (!s.equals("end")) {
                Writer.write(s);
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        toWrite.forEach(s::append);
        return s.toString();
    }
}
