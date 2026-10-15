package kz.qonaq.storage;

import kz.qonaq.model.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/** Versioned plain-text persistence; atomic rename protects completed saves. */
public final class OrderRepository {
    private final Path file;
    public OrderRepository(Path file) { this.file=file; }
    private static String e(String s) { return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8)); }
    private static String d(String s) { return new String(Base64.getUrlDecoder().decode(s),StandardCharsets.UTF_8); }
    public List<Order> load() {
        List<Order> result = new ArrayList<>();
        if (!Files.isRegularFile(file)) return result;
        try (BufferedReader reader=Files.newBufferedReader(file,StandardCharsets.UTF_8)) {
            String line;
            while ((line=reader.readLine())!=null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                try {
                    String[] p=line.split("\\|",-1);
                    if (p.length!=13 || !p[0].equals("v1")) continue;
                    List<CartLine> items = new ArrayList<>();
                    for (String s:p[12].split(",")) {
                        String[] a=s.split(":",-1);
                        if (a.length==5) items.add(new CartLine(d(a[0]),d(a[1]),Integer.parseInt(a[2]),Integer.parseInt(a[3]),d(a[4])));
                    }
                    result.add(new Order(d(p[1]),d(p[2]),d(p[3]),d(p[4]),d(p[5]),d(p[6]),d(p[7]),
                        LocalDateTime.parse(p[8]),items,Integer.parseInt(p[9]),Integer.parseInt(p[10]),OrderStatus.valueOf(p[11])));
                } catch (RuntimeException ignored) { /* skip corrupted row, keep other records */ }
            }
        } catch (IOException ex) { System.err.println("Order load failed: " + ex.getMessage()); }
        return result;
    }
    public void save(List<Order> orders) {
        try {
            Files.createDirectories(file.getParent());
            Path temp=file.resolveSibling(file.getFileName()+".tmp");
            try (BufferedWriter writer=Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                writer.write("# QONAQ Restaurant Studio / orders / version 1"); writer.newLine();
                for (Order o:orders) {
                    List<String> lines=new ArrayList<>();
                    for (CartLine line:o.items()) lines.add(String.join(":",e(line.productId()),e(line.name()),
                            ""+line.unitPrice(),""+line.quantity(),e(line.note())));
                    String row=String.join("|","v1",e(o.id()),e(o.customer()),e(o.phone()),e(o.fulfillment()),
                        e(o.location()),e(o.comment()),e(o.promo()),o.createdAt().toString(),""+o.subtotal(),
                        ""+o.discount(),o.status().name(),String.join(",",lines));
                    writer.write(row); writer.newLine();
                }
            }
            try { Files.move(temp,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temp,file,StandardCopyOption.REPLACE_EXISTING); }
        } catch (IOException ex) { throw new UncheckedIOException("Тапсырыстарды сақтау мүмкін болмады",ex); }
    }
    public Path file() { return file; }
}
