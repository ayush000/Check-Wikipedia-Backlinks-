package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.*;

public class Main {
    public static String getLastBitFromUrl(final String url){
        // return url.replaceFirst("[^?]*/(.*?)(?:\\?.*)","$1);" <-- incorrect
        return url.replaceFirst(".*/([^/?]+).*", "$1");
    }

    private static String readAll(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromTxt(String jsonText) throws IOException, JSONException {
//        try {
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
//        } finally {
//            is.close();
//        }
    }

    public static void main(String[] args) throws IOException, JSONException {
//        System.out.println(args[0]);
        String limit="max";
        String title=getLastBitFromUrl(args[0]);
        if(args.length>1)
        {
            limit=args[1];
        }
        String json=readAll("https://en.wikipedia.org/w/api.php?action=query&titles="+title+"&prop=links&pllimit="+limit+"&format=json");
//        System.out.println(json);
        JSONObject p=readJsonFromTxt(json).getJSONObject("query").getJSONObject("pages");
        String s=(String)p.names().get(0);
        JSONArray links=p.getJSONObject(s).getJSONArray("links");
        int numOfLinks=links.length();
        System.out.println("checking "+numOfLinks+" links for popularity/backlinks");
        int pop_num_backlinks=0;
        String pop_url="";
        for (int i=0;i<numOfLinks;i++)
        {
            String page_name=((JSONObject)links.get(i)).getString("title").replaceAll("\\s","_");

            int bl=Integer.parseInt(readAll("http://dispenser.homenet.org/~dispenser/cgi-bin/backlinkscount.py?title=" + page_name).replaceAll("[^\\d.]", ""));
            String url_construct="https://en.wikipedia.org/wiki/"+page_name;
            System.out.println(url_construct+" has "+bl+" backlinks.");
            if(bl>pop_num_backlinks)
            {
                pop_num_backlinks=bl;
                pop_url=url_construct;
            }
//            System.out.println("bl is "+bl);
        }

        System.out.println("Most popular url is "+pop_url+" and has "+pop_num_backlinks+" backlinks.");
    }
}