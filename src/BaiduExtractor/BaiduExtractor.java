package BaiduExtractor;

import java.util.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.apache.commons.lang3.StringUtils;
import org.json.*;

public class BaiduExtractor {
    public Map<String, Map<String, String>> infoboxTemplate;
    public Map<String, Map<String, String>> outlineTemplate;
    public Map<String, Map<String, String>> titleTemplate;
    public Map<String, Map<String, String>> descriptionTemplate;
    public Map<String, Map<String, String>> imagesTemplate;
    public Map<String, Map<String, String>> referencesTemplate;
    public Map<String, Map<String, String>> tagsTemplate;

    public Set<String> imagesSet;
    public Set<String> referencesSet;
    public Set<String> tagsSet;
    public Set<String> linksSet;
    public Set<String> kvSet;
    
    public static String sourceDir;
    public static String resultDir;
//    public static String sourceDir = "/home/peter/BaiduBaikeDataProcess/extraData/"; // extraData
//    public static String sourceDir = "/mnt/server66/lockeData/";
//    public static String resultDir = "/home/peter/BaiduBaikeDataProcess/result/";
//    public static String sourceDir = "C:/Users/Locke/Desktop/test/";
//    public static String resultDir = "C:/Users/Locke/Desktop/result/";
    
    private static String getDefaultCharSet() {  
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());  
        String enc = writer.getEncoding();  
        return enc;  
    }

    public static void main(String args[]) throws Exception { 
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("file.encoding=" + System.getProperty("file.encoding"));
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("Default Charset in Use=" + getDefaultCharSet()); // -Dfile.encoding=UTF-8  

        sourceDir = System.getProperty("inputDir"); // -DinputDir="/mnt/server66/lockeData/"
		resultDir = System.getProperty("outputDir"); // -DoutputDir="/home/peter/BaiduBaikeDataProcess/result/"
        File input = new File(sourceDir);
		if (input.exists()==false) {
			System.out.println("FAULT: inputDir has no source files.");
			return;
		}
		File output = new File(resultDir);
		if (output.exists()==false) {
			System.out.println("WARNING: outputDir doesn't exist. Having been created now.");
			output.mkdir();
		}
		
        for (File file : input.listFiles()) {
            BaiduExtractor bE = new BaiduExtractor();
            bE.processing(file.getName());
        }
        
//        if (args.length > 0) {
//            for (String filename : args) {
//                BaiduExtractor bE = new BaiduExtractor();
//                bE.processing(filename);
//            }
//        }

        System.out.println("Done! :D");
    }

    public BaiduExtractor() {
        infoboxTemplate = new HashMap<String, Map<String, String>>();
        outlineTemplate = new HashMap<String, Map<String, String>>();
        titleTemplate = new HashMap<String, Map<String, String>>();
        descriptionTemplate = new HashMap<String, Map<String, String>>();
        imagesTemplate = new HashMap<String, Map<String, String>>();
        referencesTemplate = new HashMap<String, Map<String, String>>();
        tagsTemplate = new HashMap<String, Map<String, String>>();

        imagesSet = new HashSet<String>();
        referencesSet = new HashSet<String>();
        tagsSet = new HashSet<String>();
        linksSet = new HashSet<String>();
        kvSet = new HashSet<String>();

        Map<String, String> ibTmpl_1 = new HashMap<String, String>();
        ibTmpl_1.put("keyType", "span");
        ibTmpl_1.put("keyClass", "biTitle");
        ibTmpl_1.put("valType", "div");
        ibTmpl_1.put("valClass", "biContent");
        infoboxTemplate.put("baseInfoWrap", ibTmpl_1);
        Map<String, String> ibTmpl_2 = new HashMap<String, String>();
        ibTmpl_2.put("keyType", "dt");
        ibTmpl_2.put("keyClass", "basicInfo-item name");
        ibTmpl_2.put("valType", "dd");
        ibTmpl_2.put("valClass", "basicInfo-item value");
        infoboxTemplate.put("basic-info cmn-clearfix", ibTmpl_2);
        Map<String, String> ibTmpl_3 = new HashMap<String, String>();
        ibTmpl_3.put("keyType", "dt");
        ibTmpl_3.put("keyClass", "basicInfo-item name");
        ibTmpl_3.put("valType", "dd");
        ibTmpl_3.put("valClass", "basicInfo-item value");
        infoboxTemplate.put("basic-info", ibTmpl_3);
        //
        Map<String, String> olTmpl_1 = new HashMap<String, String>();
        olTmpl_1.put("valType", "li");
        olTmpl_1.put("valClass", "level");
        outlineTemplate.put("lemma-catalog", olTmpl_1);
        Map<String, String> olTmpl_2 = new HashMap<String, String>();
        olTmpl_2.put("valType", "a");
        olTmpl_2.put("valClass", "true");
        outlineTemplate.put("z-catalog nslog-area log-set-param", olTmpl_2);
        //
        Map<String, String> tiTmpl_1 = new HashMap<String, String>();
        tiTmpl_1.put("valType", "span");
        tiTmpl_1.put("valClass", "lemmaTitleH1");
        titleTemplate.put("lemmaTitleH1_span", tiTmpl_1);
        Map<String, String> tiTmpl_2 = new HashMap<String, String>();
        tiTmpl_2.put("valType", "dd");
        tiTmpl_2.put("valClass", "lemmaWgt-lemmaTitle-title");
        titleTemplate.put("lemmaWgt-lemmaTitle-title", tiTmpl_2);
        Map<String, String> tiTmpl_3 = new HashMap<String, String>();
        tiTmpl_3.put("valType", "div");
        tiTmpl_3.put("valClass", "lemmaTitleBox clearfix");
        titleTemplate.put("lemmaTitleBox clearfix", tiTmpl_3);
        Map<String, String> tiTmpl_4 = new HashMap<String, String>();
        tiTmpl_4.put("valType", "div");
        tiTmpl_4.put("valClass", "lemmaTitleH1");
        titleTemplate.put("lemmaTitleH1_div", tiTmpl_4);
        //
        Map<String, String> deTmpl_1 = new HashMap<String, String>();
        deTmpl_1.put("valType", "div");
        deTmpl_1.put("valClass", "card-summary-content");
        descriptionTemplate.put("card-summary-content", deTmpl_1);
        Map<String, String> deTmpl_2 = new HashMap<String, String>();
        deTmpl_2.put("valType", "div");
        deTmpl_2.put("valClass", "lemma-summary");
        descriptionTemplate.put("lemma-summary", deTmpl_2);
        //
        Map<String, String> tgTmpl_1 = new HashMap<String, String>();
        tgTmpl_1.put("valType", "span");
        tgTmpl_1.put("valClass", "taglist");
        tagsTemplate.put("taglist_span", tgTmpl_1);
        Map<String, String> tgTmpl_2 = new HashMap<String, String>();
        tgTmpl_2.put("valType", "sapn");
        tgTmpl_2.put("valClass", "taglist");
        tagsTemplate.put("taglist_sapn", tgTmpl_2);
        //
        Map<String, String> reTmpl_1 = new HashMap<String, String>();
        reTmpl_1.put("valType", "li");
        reTmpl_1.put("valClass", "reference-item");
        referencesTemplate.put("reference-item", reTmpl_1);
        Map<String, String> reTmpl_2 = new HashMap<String, String>();
        reTmpl_2.put("valType", "p");
        reTmpl_2.put("valClass", "refUrl");
        referencesTemplate.put("refUrl", reTmpl_2);
        //
        Map<String, String> imTmpl_1 = new HashMap<String, String>();
        imTmpl_1.put("valType", "div");
        imTmpl_1.put("valClass", "main-content");
        imagesTemplate.put("main-content", imTmpl_1);
        Map<String, String> imTmpl_2 = new HashMap<String, String>();
        imTmpl_2.put("valType", "div");
        imTmpl_2.put("valClass", "u-page");
        imagesTemplate.put("u-page", imTmpl_2);
    }

    public void processing(String filename) throws Exception {
    	System.out.println("Processing file: " + filename);
        BufferedReader bufferedReaderRaw = new BufferedReader(new FileReader(new File(sourceDir + filename)));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(resultDir + filename.replace(".", ".result."))));
//        BufferedWriter bufferedWriterProblem = new BufferedWriter(new FileWriter(new File(resultDir + "/" + filename.replace(".", ".problem."))));
//        BufferedWriter bufferedWriterUrl = new BufferedWriter(new FileWriter(new File(resultDir + "/" + filename.replace(".", ".url."))));
        int count = 0;        
        String line = new String();
        List<String> list = new ArrayList<String>();
        while (true) {
            line = bufferedReaderRaw.readLine();
//            System.out.println(line);
            if (line == null) {
                break;
            }
            if (line.equals("")) {
                count += 1;
                JSONObject temp = new JSONObject(StringUtils.join(list, ""));
                JSONObject page = new JSONObject();
                String rawHtml = temp.get("html").toString();
//                long s1 = System.nanoTime();
                Document doc = Jsoup.parse(rawHtml, "UTF-8");
//                long s2 = System.nanoTime();
                page.put("url", temp.getString("url").replaceAll("\t", ""));   
                page.put("infobox", getInfobox(doc));
                page.put("outline", getOutline(doc));
                page.put("title", getTitle(doc, temp.getString("content_title")));
                page.put("synonym", getSynonym(doc));
                page.put("polyseme", getPolyseme(doc));
                page.put("description", getDescription(doc));
                page.put("content", getContent(doc));
                page.put("images", getImages(doc));
                page.put("tags", getTags(doc));
                page.put("links", getLinks());
                page.put("references", getReferences(doc));
                page.put("statistics", getStatistics(doc));
//                long s3 = System.nanoTime();
//                System.out.println("Jsoup.parse: "+ (s2-s1)/1000000000.0 +"s");
//                System.out.println("Process: "+ (s3-s2)/1000000000.0 +" s");
                list.clear();
//                System.out.println(page.toString());
                
                if (rawHtml.equals("<html><head></head><body></body></html>\n") || rawHtml.equals("") || page.getJSONObject("title").length()==0) {
//                    bufferedWriterProblem.write(temp.toString() + "\n");
//                    bufferedWriterUrl.write(page.getString("url") + "\n");
                } else {
                    bufferedWriter.write(page.toString() + "\n");
                }
            } else {
                list.add(line);
            }
        }
        bufferedReaderRaw.close();
        bufferedWriter.close();
//        bufferedWriterProblem.close();
//        bufferedWriterUrl.close();
        System.out.println("__Total: " + count);
    }

    public void testProcessing(String filename) throws Exception {
    	System.out.println("Processing file: " + filename);
        BufferedReader bufferedReaderRaw = new BufferedReader(new FileReader(new File(sourceDir + filename)));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(resultDir + filename.replace(".", ".result."))));
//        BufferedWriter bufferedWriterProblem = new BufferedWriter(new FileWriter(new File(resultDir + "/" + filename.replace(".", ".problem."))));
//        BufferedWriter bufferedWriterUrl = new BufferedWriter(new FileWriter(new File(resultDir + "/" + filename.replace(".", ".url."))));
        String line = new String();
        List<String> list = new ArrayList<String>();
        while (true) {
            line = bufferedReaderRaw.readLine();
//            System.out.println(line);
            if (line == null) {
                JSONObject page = new JSONObject();
                String rawHtml = StringUtils.join(list, "");
//                long s1 = System.nanoTime();
                Document doc = Jsoup.parse(rawHtml, "UTF-8");
//                long s2 = System.nanoTime();  
                page.put("infobox", getInfobox(doc));
                page.put("outline", getOutline(doc));
                page.put("title", getTitle(doc, ""));
                page.put("synonym", getSynonym(doc));
                page.put("polyseme", getPolyseme(doc));
                page.put("description", getDescription(doc));
                page.put("content", getContent(doc));
                page.put("images", getImages(doc));
                page.put("tags", getTags(doc));
                page.put("links", getLinks());
                page.put("references", getReferences(doc));
                page.put("statistics", getStatistics(doc));
//                long s3 = System.nanoTime();
//                System.out.println("Jsoup.parse: "+ (s2-s1)/1000000000.0 +"s");
//                System.out.println("Process: "+ (s3-s2)/1000000000.0 +" s");
                list.clear();
                System.out.println(page.toString());
                
                if (rawHtml.equals("<html><head></head><body></body></html>\n") || rawHtml.equals("") || page.getJSONObject("title").length()==0) {
//                    bufferedWriterProblem.write(temp.toString() + "\n");
//                    bufferedWriterUrl.write(page.getString("url") + "\n");
                } else {
                    bufferedWriter.write(page.toString() + "\n");
                }
                break;
            } else {
                list.add(line);
            }
        }
        bufferedReaderRaw.close();
        bufferedWriter.close();
//        bufferedWriterProblem.close();
//        bufferedWriterUrl.close();
    }
    
    public String clearString(String str) {
        str = str.replaceAll("\u00A0", "").replaceAll("\n", "").replaceAll("&nbsp;", "").replaceAll("&nbsp", "").trim();
        str = str.replaceAll("\\<.*?>", ""); 
        return str;
    }

    public JSONObject getSynonym(Document doc) {
        JSONObject synonym = new JSONObject();
        try {
            Elements spansV = doc.select("span[class=view-tip-panel]");
            if (spansV.size() == 1) {
//                System.out.println("__ _" + spansV);
                if (spansV.size() != 0) {
                    ArrayList<String> aList = new ArrayList<String>();
                    for (Node node : spansV.get(0).childNodes()) {
                        if (node.nodeName().equals("#text")) {
                            aList.add(node.toString());
                        } else if (node.nodeName().equals("a")) {
                            continue;
                        } else if (node.nodeName().equals("span")) {
                            aList.add(((Element)node).text());
                        }
                    }
                    String ss[] = StringUtil.join(aList, "").split("一般指");
                    synonym.put("from", clearString(ss[0])); // ref
                    synonym.put("to", clearString(ss[1])); // main
                }
            }
//            System.out.println(synonym);
            return synonym;
        } catch (Exception e) {
            return synonym;
        }
    }
    
    public JSONArray getPolyseme(Document doc) {
    	JSONArray polyseme = new JSONArray();
        try {
            Elements divsV = doc.select("div[class=polysemeBody]");
            if (divsV.size() == 1) {
//                System.out.println("__ _" + divsV);
                for (Element li : divsV.get(0).getElementsByTag("li")) {
                	polyseme.put(clearString(li.text().substring(1)));                    
                }
            } else {
            	divsV = doc.select("div[class*=polysemant-list]");
            	if (divsV.size() == 1) {
//                  System.out.println("__ _" + divsV);
                    for (Element li : divsV.get(0).getElementsByTag("li")) {
                    	polyseme.put(clearString(li.text().substring(1)));                    
                    }
            	}
            }
//            System.out.println(polyseme);
            return polyseme;
        } catch (Exception e) {
            return polyseme;
        }
    }
    
    public JSONObject getTitle(Document doc, String temp_title) {
    	JSONObject title = new JSONObject();
        try {
            for (String key : titleTemplate.keySet()) {
                Elements valBlocks = doc.select(titleTemplate.get(key).get("valType").toString() + "[class*=" + titleTemplate.get(key).get("valClass").toString() + "]");
//                System.out.println("__ _" + valBlocks);
                if (valBlocks.size() >= 1) {
                    if (key.equals("lemmaTitleH1_span") || key.equals("lemmaTitleH1_div")) {
                    	String temp_str = valBlocks.text();
                    	if (temp_str.equals("")==false) {
//                    		System.out.println("__ _>>>" + valBlocks.get(0).getElementsByTag("span").size() + "+ +++");
                    		if (valBlocks.get(0).getElementsByTag("span").size()>=1 && temp_str.indexOf("（")!=-1) {
	                            title.put("h1", temp_str.substring(0, temp_str.indexOf("（")));
	                            title.put("h2", temp_str.substring(temp_str.indexOf("（")));
                    		} else {
                    			title.put("h1", temp_str);
                    			title.put("h2", "");
                    		}
                    	}
                    } else { // lemmaWgt-lemmaTitle-title // lemmaTitleBox // clearfix // dt_title
                    	title.put("h1", valBlocks.get(0).getElementsByTag("h1").text());
                    	title.put("h2", valBlocks.get(0).getElementsByTag("h2").text());
                    }
                    if (title.getString("h1").equals("")) {
                    	title.put("h1", temp_title);
                    }
                    break;
                }
            }
            return title;
        } catch (Exception e) {
            return title;
        }
    }

    public String getDescription(Document doc) {
        String description = new String();
        try {
            for (String key : descriptionTemplate.keySet()) {
                Elements valBlocks = doc.select(descriptionTemplate.get(key).get("valType").toString() + "[class*="
                        + descriptionTemplate.get(key).get("valClass").toString() + "]");
                if (valBlocks.size() >= 1) {
                    List<String> desList = new ArrayList<String>();
                    List<String> list = new ArrayList<String>();
                    for (Node ele : valBlocks.get(0).childNodes()) {
//                        System.out.println("__ _" + ele.nodeName());
                        boolean last = false;
                        if (ele.nodeName().equals("div")) {
                            for (Element para : valBlocks.get(0).children()) {
                                for (Node node : para.childNodes()) {
//                                    System.out.println("__ _" + node.nodeName());
                                    if (node.nodeName().equals("a")) {
                                        if (last == false && clearString(node.childNodes().get(0).toString()).equals("")==false && node.attr("href").equals("")==false) {
                                            list.add("[[");
                                            list.add(clearString(node.childNodes().get(0).toString()));
                                            list.add("||");
                                            list.add(node.attr("href"));
                                            list.add("]]");
                                            linksSet.add("[["+clearString(node.childNodes().get(0).toString())+"||"+node.attr("href")+"]]");
                                        }
                                    } else if (node.nodeName().equals("sup")) {
                                        last = true;
                                    } else if (node.nodeName().equals("b")) {
                                        list.add(node.childNode(0).toString());
                                        last = false;
                                    } else if (node.nodeName().equals("#text")) {
                                        list.add(node.toString());
                                        last = false;
                                    }
                                }
                                desList.add(clearString(StringUtils.join(list, "")));
                                list.clear();
                            }
                            break;
                        } else if (ele.nodeName().equals("a")) {
                            if (last == false && clearString(ele.childNodes().get(0).toString()).equals("")==false && ele.attr("href").equals("")==false) {
                                list.add("[[");
                                list.add(clearString(ele.childNodes().get(0).toString()));
                                list.add("||");
                                list.add(ele.attr("href"));
                                list.add("]]");
                                linksSet.add("[["+clearString(ele.childNodes().get(0).toString())+"||"+ele.attr("href")+"]]");
                            }
                        } else if (ele.nodeName().equals("sup")) {
                            last = true;
                        } else if (ele.nodeName().equals("b")) {
                            list.add(ele.childNode(0).toString());
                            last = false;
                        } else if (ele.nodeName().equals("#text")) {
                            list.add(ele.toString());
                            last = false;
                        }
                        desList.add(clearString(StringUtils.join(list, "")));
                        list.clear();
                    }
                    description = StringUtils.join(desList, "");
                    break;
                }
            }
            return description;
        } catch (Exception e) {
            return description;
        }
    }

    public String getContent(Document doc) {
        List<String> text_contentList = new ArrayList<String>();
        try {
            Elements valBlocks = doc.select("div[class*=lemma-main-content]");
//            System.out.println("__ _" + valBlocks);
            if (valBlocks.size() > 0) {
//                System.out.println("__ _" + valBlocks.get(0));
                for (Element element : valBlocks.get(0).children()) {
//                    System.out.println("__ _" + element.tagName());
                    if (element.tagName().equals("div") && element.attr("class").equals("para")) {
                        List<String> list = new ArrayList<String>();
                        boolean last = false;
                        for (Node node : element.childNodes()) {
//                            System.out.println("__ _" + node.nodeName());
                            if (node.nodeName().equals("a") && node.hasAttr("class") == false) {
                                if (last == false && node.childNodes().get(0).toString().equals("")==false && node.attr("href").equals("")==false) {
                                    list.add("[[");
                                    list.add(node.childNodes().get(0).toString());
                                    list.add("||");
                                    list.add(node.attr("href"));
                                    list.add("]]");
                                    linksSet.add("[["+clearString(node.childNodes().get(0).toString())+"||"+node.attr("href")+"]]");
                                }
                            } else if (node.nodeName().equals("sup")) {
                                last = true;
                            } else if (node.nodeName().equals("b")) {
                                list.add(node.childNode(0).toString());
                                last = false;
                            } else if (node.nodeName().equals("#text")) {
                                list.add(node.toString());
                                last = false;
                            }
                        }
                        String para = StringUtils.join(list, "").replaceAll("\n", "").trim();
                        if (para.equals("") == false) {
                            text_contentList.add(para);
                        }
                    } else if (element.tagName().equals("h2") && element.attr("class").equals("headline-1")) {
//                        System.out.println("__ _" + element.text());
                        for (Element node : element.children()) {
                            if (node.tagName().equals("span") && node.attr("class").equals("headline-content")) {
                                text_contentList.add("~~~~~" + node.text().trim());
                            }
                        }
                    } else if (element.tagName().equals("h3") && element.attr("class").equals("headline-2")) {
                      for (Element node : element.children()) {
                          if (node.tagName().equals("span") && node.attr("class").equals("headline-content")) {
                              text_contentList.add("·····" + node.text().trim());
                          }
                      }
                  }
                }
            } else {
                valBlocks = doc.select("div[class*=main_tab main_tab-defaultTab]");
//                System.out.println("__ _" + valBlocks);
                if (valBlocks.size() == 0) {
                    valBlocks = doc.select("div[class=main-content]");
                }
                if (valBlocks.size() > 0) {
                    for (Element element : valBlocks.get(0).children()) {
//                        System.out.println("__ _" + element.tagName());
                        if (element.tagName().equals("div") && element.attr("class").equals("para")) {
                            List<String> list = new ArrayList<String>();
                            boolean last = false;
                            for (Node node : element.childNodes()) {
//                                System.out.println("__ _" + node.nodeName());
                                if (node.nodeName().equals("a") && node.hasAttr("class") == false) {
                                    if (last == false && clearString(node.childNodes().get(0).toString()).equals("")==false && node.attr("href").equals("")==false) {
                                        list.add("[[");
                                        list.add(clearString(node.childNodes().get(0).toString()));
                                        list.add("||");
                                        list.add(node.attr("href"));
                                        list.add("]]");
                                        linksSet.add("[["+clearString(node.childNodes().get(0).toString())+"||"+node.attr("href")+"]]");
                                    }
                                } else if (node.nodeName().equals("sup")) {
                                    last = true;
                                } else if (node.nodeName().equals("b")) {
                                    list.add(node.childNode(0).toString());
                                    last = false;
                                } else if (node.nodeName().equals("#text")) {
                                    list.add(node.toString());
                                    last = false;
                                }
                            }
                            String para = StringUtils.join(list, "").replaceAll("\n", "").trim();
                            if (para.equals("") == false) {
                                text_contentList.add(para);
                            }
                        } else if (element.tagName().equals("div")
                                && element.attr("class").equals("para-title level-2")) {
                            for (Node node : element.getElementsByTag("h2").get(0).childNodes()) {
                                if (node.nodeName().equals("#text")) {
                                	text_contentList.add("~~~~~" + node.toString().trim());
                                }
                            }
                        } else if (element.tagName().equals("div")
                                && element.attr("class").equals("para-title level-3")) {
                            for (Node node : element.getElementsByTag("h3").get(0).childNodes()) {
                                if (node.nodeName().equals("#text")) {
                                	text_contentList.add("·····" + node.toString().trim());
                                }
                            }
                        }
                    }
                }
            }
            return StringUtils.join(text_contentList, "*****");
        } catch (Exception e) {
            return StringUtils.join(text_contentList, "*****");
        }
    }

    public JSONArray getLinks() {
        JSONArray inner_link = new JSONArray();
        linksSet.remove("[[||]]");
        for (String key : linksSet) {
            inner_link.put(key);
        }
        linksSet.clear();
//      System.out.println(inner_link);
        return inner_link;
    }

    public JSONArray getReferences(Document doc) {
        JSONArray references = new JSONArray();
        try {
            for (String key : referencesTemplate.keySet()) {
                Elements valBlocks = doc.select(referencesTemplate.get(key).get("valType").toString() + "[class=" + referencesTemplate.get(key).get("valClass").toString() + "]");
                if (valBlocks.size() > 0) {
                    // System.out.println(valBlocks);
                    for (Element li : valBlocks) {
                        for (Node node : li.childNodes()) {
                            if (node.nodeName().equals("a") && node.attr("rel").equals("nofollow")) {
                            	references.put("[[" + clearString(node.childNode(0).toString()) + "||" + node.attr("href") + "]]");
                            }
                        }
                    }
                    break;
                }
            }
            return references;
        } catch (Exception e) {
            return references;
        }
    }
    
    public JSONArray getImages(Document doc) {
        JSONArray images = new JSONArray();
        try {
            Elements summaryPicBlock = doc.select("div[class=summary-pic]");
            if (summaryPicBlock.size() > 0) {
            	images.put("[[" + clearString(summaryPicBlock.get(0).text()) + "||"
                        + summaryPicBlock.get(0).select("img").get(0).attr("src") + "]]");
            }
            for (String key : imagesTemplate.keySet()) {
                Elements valBlocks = doc.select(imagesTemplate.get(key).get("valType").toString() + "[class=" + imagesTemplate.get(key).get("valClass").toString() + "]");
                if (valBlocks.size() > 0) {
                    Elements imageBox = valBlocks.get(0).select("img[class]");
                    // System.out.println(imageBox);
                    for (Element element : imageBox) {
                    	images.put("[[" + clearString(element.attr("alt")) + "||" + element.attr("src") + "]]");
                    }
                    break;
                }
            }
            return images;
        } catch (Exception e) {
            return images;
        }
    }
    
    public JSONArray getTags(Document doc) {
        JSONArray tags = new JSONArray();
        try {
            for (String key : tagsTemplate.keySet()) {
                Elements valBlocks = doc.select(tagsTemplate.get(key).get("valType").toString() + "[class="
                        + tagsTemplate.get(key).get("valClass").toString() + "]");
                if (valBlocks.size() > 0) {
                    for (Element span : valBlocks) {
                        for (Node node : span.childNodes()) {
                            if (node.nodeName().equals("#text")) {
                                tags.put(clearString(node.toString()));
                            } else if (node.nodeName().equals("a")) {
                                tags.put("[[" + clearString(span.child(0).text()) + "||" + node.attr("href") + "]]");
                            }
                        }
                    }
                    break;
                }
            }
            return tags;
        } catch (Exception e) {
            return tags;
        }
    }

    public JSONObject getInfobox(Document doc) {
        JSONObject infobox = new JSONObject();
        try {
            for (String key : infoboxTemplate.keySet()) {
                Elements keyBlocks = doc.select(infoboxTemplate.get(key).get("keyType").toString() + "[class*="
                        + infoboxTemplate.get(key).get("keyClass").toString() + "]");
                Elements valBlocks = doc.select(infoboxTemplate.get(key).get("valType").toString() + "[class*="
                        + infoboxTemplate.get(key).get("valClass").toString() + "]");

                if (keyBlocks.size() == valBlocks.size() && keyBlocks.size() != 0) {
                    for (int i = 0; i < keyBlocks.size(); i += 1) {
                        List<String> list = new ArrayList<String>();

                        for (Node node : valBlocks.get(i).childNodes()) {
                            if (node.nodeName().equals("a") && clearString(node.childNodes().get(0).toString()).equals("")==false && node.attr("href").equals("")==false) {
                                list.add("[[");
                                list.add(clearString(node.childNodes().get(0).toString()));
                                list.add("||");
                                list.add(node.attr("href"));
                                list.add("]]");
                                linksSet.add("[["+clearString(node.childNodes().get(0).toString())+"||"+node.attr("href")+"]]");
                            } else if (node.nodeName().equals("br")) {
                            } else {
                                list.add(node.toString());
                                list.add(" ");
                            }
                        }
                        infobox.put(clearString(keyBlocks.get(i).text()), clearString(StringUtils.join(list, "")));
                    }
                    break;
                }
            }
            return infobox;
        } catch (Exception e) {
            return infobox;
        }
    }

    public JSONObject getOutline(Document doc) {
        JSONObject outline = new JSONObject();
        try {
            for (String key : outlineTemplate.keySet()) {
                Elements valBlocks = doc.select(outlineTemplate.get(key).get("valType").toString() + "["
                        + (key == "lemma-catalog" ? "class" : "catalog") + "^="
                        + outlineTemplate.get(key).get("valClass").toString() + "]");
                String last = new String();
                if (valBlocks.size() != 0) {
                    for (Element element : valBlocks) {
                        String order = (key == "lemma-catalog"
                                ? element.children().get(1).children().attr("href").substring(1)
                                : element.attr("href").substring(1));
                        String val = (key == "lemma-catalog" ? element.children().get(1).text() : element.text());
                        val = clearString(val);

                        if (order.indexOf('_') == -1) {
                            outline.put(order + "~" + val, new JSONObject());
                            last = order + "~" + val;
                        } else {
                            outline.getJSONObject(last).put(order + "~" + val, new JSONObject());
                        }
                    }
                    break;
                }
            }
            return outline;
        } catch (Exception e) {
            return outline;
        }
    }

    public JSONObject getStatistics(Document doc) {
    	JSONObject statistics = new JSONObject();
    	try {
            Elements lisV = doc.select("[class*=side-box lemma-statistics]");
            if (lisV.size()==1) {
            	Elements lis = lisV.get(0).select("li");
        		if (lis.size()==4) {
        			statistics.put("pv", lis.get(0).text().replaceAll("[^(0-9)]", ""));
        			statistics.put("edit_times", lis.get(1).text().replaceAll("[^(0-9)]", ""));
        			statistics.put("last_modified", lis.get(2).text().substring(lis.get(2).text().indexOf("：")+1));
        			statistics.put("creator", lis.get(3).text().substring(lis.get(3).text().indexOf("：")+1));
        		}
            } else {
            	Elements divsV = doc.select("[class*=side-box side-box-extend]");
            	if (divsV.size()==1) {
            		Elements divs = divsV.get(0).select("[class=side-list-item]");
            		if (divs.size()==4) {
            			statistics.put("pv", divs.get(0).text().replaceAll("[^(0-9)]", ""));
            			statistics.put("edit_times", divs.get(1).text().replaceAll("[^(0-9)]", ""));
            			statistics.put("last_modified", divs.get(2).text().substring(divs.get(2).text().indexOf("：")+1));
            			statistics.put("creator", divs.get(3).text().substring(divs.get(3).text().indexOf("：")+1));
            		}
            	}
            }
            return statistics;
        } catch (Exception e) {
            return statistics;
        }
	}
}
