package kz.qonaq.tests;

import kz.qonaq.model.*;
import kz.qonaq.patterns.facade.RestaurantFacade;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Dependency-free integration smoke test, runnable as a regular Java class. */
public final class SmokeTest {
    private static int assertions=0;
    private static void check(boolean passed,String description){
        assertions++;if(!passed)throw new AssertionError(description);
    }
    private static void expectFail(Runnable action,String message){
        boolean failed=false;
        try{action.run();}catch(IllegalArgumentException|IllegalStateException expected){failed=true;}
        check(failed,message);
    }
    public static void main(String[] args)throws Exception{
        Path temp=Files.createTempDirectory("qonaq-test");
        Path storage=temp.resolve("orders.txt");
        RestaurantFacade app=new RestaurantFacade(storage);
        check(app.catalog().size()==12,"Factory Method builds a 12-item catalog");
        check(app.orders().size()==7,"Seed contains 7 demo orders");
        app.removeDemo();
        check(app.orders().isEmpty(),"Demo removal works");
        AtomicInteger received=new AtomicInteger(0);
        app.events().subscribe((order,message)->received.incrementAndGet());
        app.addToCart("F01",2);app.addToCart("D01",1);
        check(app.cartQuantity()==3,"Cart stores quantities");
        expectFail(()->app.addToCart("F01",49),"Quantity limit enforced");
        expectFail(()->app.setPromo("FAKE"),"Invalid promo blocked");
        app.setPromo("QONAQ10");
        check(app.discount()==(int)Math.round(app.subtotal()*0.1),"Discount applied");
        expectFail(()->app.checkout("","0771234567","Жеткізу","", ""),"Builder validates customer");
        expectFail(()->app.checkout("Аян","abc","Жеткізу","", ""),"Builder validates phone");
        expectFail(()->app.checkout("Аян","+7 777 123 44 55","Жеткізу","", ""),"Builder validates delivery address");
        Order order=app.checkout("Аян","+7 777 123 44 55","Жеткізу","Алматы, Абая 1","Пиязсыз");
        check(app.cartQuantity()==0,"Checkout clears cart");
        check(order.status()==OrderStatus.NEW,"Initial State is NEW");
        check(received.get()==1,"Observer notified of new order");
        check(Files.exists(storage),"Persistence file created");
        app.advance(order.id());check(order.status()==OrderStatus.PREPARING,"NEW -> PREPARING");
        app.advance(order.id());check(order.status()==OrderStatus.READY,"PREPARING -> READY");
        expectFail(()->app.cancel(order.id()),"READY cannot be cancelled");
        app.advance(order.id());check(order.status()==OrderStatus.COMPLETED,"READY -> COMPLETED");
        expectFail(()->app.advance(order.id()),"COMPLETED cannot advance");
        check(received.get()==4,"Observer receives all transitions");
        check(app.revenue()==order.total(),"Analytics completed revenue");
        RestaurantFacade reloaded=new RestaurantFacade(storage);
        check(reloaded.orders().size()==1,"Persistence reload does not seed duplicates");
        check(reloaded.find(order.id()).status()==OrderStatus.COMPLETED,"Reloaded order state retained");
        check(reloaded.find(order.id()).items().size()==2,"Reloaded order lines retained");
        reloaded.addToCart("S01",1);
        Order second=reloaded.checkout("Арман","+7 700 123 45 67","Алып кету","", "");
        reloaded.cancel(second.id());
        check(second.status()==OrderStatus.CANCELLED,"Cancel from NEW works");
        check(reloaded.revenue()==order.total(),"Cancelled orders excluded from revenue");
        System.out.println("SMOKE_TEST_OK: "+assertions+" assertions passed");
    }
}
