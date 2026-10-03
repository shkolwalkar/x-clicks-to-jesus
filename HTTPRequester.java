import java.net.*;
import java.net.http.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.*;
import java.io.*;
import java.net.http.HttpResponse.*;

import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;


public class HTTPRequester {
   private static int numClicks;
   private static Scanner readUser = new Scanner(System.in);
   public static ArrayList<String> seeds = new ArrayList<String>();
   private static HttpClient client = HttpClient.newBuilder().build();
      
   public static void main(String[] args) {
      playGame();
   }
   
   public static void playGame() {
      try {
         System.out.println("Welcome to X Clicks to Jesus!");
         Thread.sleep(1500);
         System.out.println("How many clicks would you like to play today?");
         Thread.sleep(1500);
         System.out.println(">8  == easy");
         System.out.println("6-8 == medium");
         System.out.println("4-6 == hard");
         System.out.println("<4  == ur cooked lil bro");
         numClicks = Integer.parseInt(readUser.nextLine());
         
         try {
            initializeArray();
         }
         catch (Exception e) {
            e.printStackTrace();
         }

         String link = seeds.get((int)(Math.random() * 100));
         while (numClicks > 0) {
            try {
               link = click(link);
            }
            catch (Exception e) {
               System.out.println("hey");
            }
         }
         System.out.println("LMAO you lost. Suck it.");
         System.exit(0);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   /*
   public static String getRandom() throws Exception {
      HttpURLConnection c = (HttpURLConnection)(new URL("https://en.wikipedia.org/wiki/Special:Random").openConnection());
      c.connect();
      int responseCode = c.getResponseCode();
      if (responseCode == 301 || responseCode == 302) {
         return c.getHeaderField("Location");
      }
      else return null;
   }
   */
   
   public static void initializeArray() throws Exception {
      File f = new File("articles.txt");
      Scanner s = new Scanner(f);
      while (s.hasNextLine()) {
         seeds.add(s.nextLine());
      }
   }
   
   public static String click(String uri) {
      System.out.println("Article " + (numClicks - 1));
      try {
         HashMap<String,String> links = dissectTags(findAllLinks(get(uri)));
         String title = findTitle(get(uri));
         if (title.equals("Jesus - Wikipedia") || title.equals("Jesus in Christianity - Wikipedia")) {
            System.out.println("Congratulations! You won the game!");
            System.exit(0); 
         }
         else {
            System.out.println("Doesn't look like Jesus... where would you like to go from here?");
            System.out.println("Available Links:");
            Set<String> keys = links.keySet();
            for (String key : keys) {
               System.out.println(key);
            }
            System.out.println("Type the name of the one you want to go to.");
            System.out.println("Please be case sensitive <3");
            String nextPage = readUser.nextLine();
            nextPage.toLowerCase();
            while (!links.containsKey(nextPage)) {
               System.out.println("Invalid value (doesn't match any of the articles)");
               nextPage = readUser.nextLine();
               nextPage.toLowerCase();
            }
            numClicks--;
            return links.get(nextPage);
         }
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return "";
   }
   
   public static String get(String uri) throws Exception {
      System.out.println(uri);
      
      
      HttpRequest request = HttpRequest.newBuilder()
         .uri(new URI(uri))
         .header("User-Agent", "Mozilla/5.0")
         .build();
      HttpResponse<String> response =
         client.send(request, BodyHandlers.ofString());
      return response.body();
   }
   
   public static ArrayList<String> findAllLinks(String html) {
      ArrayList<String> htmlTags = new ArrayList<String>();
      String tag = "<a(.*?)/a>";
      Pattern p = Pattern.compile(tag);
      Matcher m = p.matcher(html);
      while (m.find()) {
         htmlTags.add(m.group());
      }
      return htmlTags;
   }
   
   public static String findTitle(String html) {
      String tag = "<meta property=\"og:title\"(.*?)>";
      Pattern p = Pattern.compile(tag);
      Matcher m = p.matcher(html);
      String titleHtml = "";
      while (m.find()) {
         titleHtml = m.group();
      }
      Document doc = Jsoup.parse(titleHtml);
      Element e = doc.select("meta").first();   
      return e.attr("content");
   }
   
   public static HashMap<String,String> dissectTags(ArrayList<String> tags) {
      HashMap<String,String> dissection = new HashMap<String,String>();
      for (String tag : tags) {
         Document doc = Jsoup.parse(tag);
         Element e = doc.select("a").first();
         
         if (e.attr("href").indexOf("/wiki") == 0) {
            String newUrl = "https://en.wikipedia.org" + e.attr("href");
            String displayText = e.text();
            displayText.toLowerCase();
            dissection.put(displayText,newUrl);
         }
      }
      return dissection;
   }
}