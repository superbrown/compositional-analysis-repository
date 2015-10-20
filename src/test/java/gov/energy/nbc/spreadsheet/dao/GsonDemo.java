//package gov.energy.nbc.spreadsheet.dao;
//
//import com.google.gson.Gson;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Map;
//
///**
// *
// * @author steve jin (http://www.doublecloud.org)
// */
//public class GsonDemo {
//    private static Gson gson = new Gson();
//
//    public static void main(String[] args) throws IOException {
//        parseJson();
//
//        saveJson();
//
//        perfTest();
//    }
//
//    public static void parseJson() throws IOException {
//        String jsonStr = "{ \"author\": \"Steve Jin\", \"title\" : \"vSphere SDK\", \"obj\" : {\"objint\" : {}} }";
//        Object obj = gson.fromJson(jsonStr, Object.class);
//
//        System.out.println("obj type: " + obj.getClass().toString()); // com.google.gson.internal.LinkedTreeMap
//        System.out.println("obj: " + obj);
//
//        Map m = gson.fromJson(jsonStr, Map.class);
//        System.out.println("m: " + m.size());  // 3
//
//        for (Object key : m.keySet()) {
//            System.out.println("key:" + key);
//        }
//
//        Book book = gson.fromJson(jsonStr, Book.class);
//        System.out.println("book: " + book);
//        System.out.println("book.author: " + book.author);
//        System.out.println("book.obj class: " + book.obj.getClass()); //com.google.gson.internal.LinkedTreeMap
//        System.out.println("book.obj: " + book.obj);
//    }
//
//    public static void saveJson() throws IOException {
//        Book book = new Book();
//        book.author = "Steve Jin";
//        book.title = "VMware vSphere and VI SDK";
//
//        String bookJson = gson.toJson(book);
//        System.out.println("bookJson: " + bookJson);
//    }
//
//    public static void perfTest() throws IOException {
//        long start = System.nanoTime();
//
//        // http://www.json-generator.com/# for generating a JSON file
//        gson.fromJson(new FileReader("src/main/resources/bigjson.json"), Map[].class);
//
//        long end = System.nanoTime();
//        System.out.println("Time taken (nano seconds): " + (end - start));
//    }
//
//    public static class Book
//    {
//        public String author;
//        public String title;
//        public Map obj;
//    }
//}
