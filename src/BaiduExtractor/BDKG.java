package BaiduExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class BDKG {

    public static void main(String args[]) throws Exception {
        BDKG bdkg = new BDKG();
        String type = System.getProperty("type");
        if (type.equals("getStatistics")) { // -Dtype=getStatistics
        	String sourceFile = System.getProperty("inputFile"); // -DinputFile="/home/peter/BaiduBaikeDataProcess/key_url_title_final.result.json"
        	File input = new File(sourceFile);
        	if (input.exists()==false) {
    			System.out.println("FAULT: inputFile has no source file.");
    			return;
    		}
        	bdkg.getStatistics(sourceFile);
        } else if (type.equals("addCategory")) { // -Dtype=addCategory
        	String sourceFile = System.getProperty("inputFile"); // -DinputFile="/home/peter/BaiduBaikeDataProcess/key_url_title_final.result.json"
        	String categoryFile = System.getProperty("categoryFile"); // -DcategoryFile="/home/peter/BaiduBaikeDataProcess/new_baidu-instance-concept.dat"
        	String resultFile = System.getProperty("outputFile");// -DoutputFile="/home/peter/BaiduBaikeDataProcess/key_url_title_final_cate.result.json"
    		File input = new File(sourceFile);
    		File cateinput = new File(categoryFile);
    		if (input.exists()==false) {
    			System.out.println("FAULT: inputFile has no source file.");
    			return;
    		}
    		if (cateinput.exists()==false) {
    			System.out.println("FAULT: categoryFile has no source file.");
    			return;
    		}
    		File output = new File(resultFile);
    		if (output.exists()==true) {
    			System.out.println("WARNING: outputFile exists. Remove it or rename the file.");
    			return;
    		}
        	bdkg.addCategory(sourceFile, categoryFile, resultFile);
        } else if (type.equals("getDumpFile")) { // -Dtype=getDumpFile
        	String sourceFile = System.getProperty("inputFile"); // -DinputFile="/home/peter/BaiduBaikeDataProcess/key_url_title_final.result.json"
    		String resultFile = System.getProperty("outputFile");// -DoutputFile="/home/peter/BaiduBaikeDataProcess/baidu-dump"
    		File input = new File(sourceFile);
    		if (input.exists()==false) {
    			System.out.println("FAULT: inputFile has no source file.");
    			return;
    		}
    		File output = new File(resultFile);
    		if (output.exists()==true) {
    			System.out.println("WARNING: outputFile exists. Remove it or rename the file.");
    			return;
    		}
        	bdkg.getDumpFile(sourceFile, resultFile);
        } else if (type.equals("getSeparateAttrs")) { // -Dtype=getSeparateAttrs
        	String sourceFile = System.getProperty("inputFile"); // -DinputFile="/home/peter/BaiduBaikeDataProcess/key_url_title_final.result.json"
    		String resultDir = System.getProperty("outputDir");// -DoutputDir="/home/peter/BaiduBaikeDataProcess/separatedAttrs/"
    		File input = new File(sourceFile);
    		if (input.exists()==false) {
    			System.out.println("FAULT: inputFile has no source file.");
    			return;
    		}
    		File output = new File(resultDir);
    		if (output.exists()==false) {
    			System.out.println("WARNING: outputDir doesn't exist. Having been created now.");
    			output.mkdir();
    		}
        	bdkg.getSeparateAttrs(sourceFile, resultDir);
        }
        System.out.println("Done! :D");
    }
    
    public void getStatistics(String sourceFile) throws Exception {
    	BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(sourceFile)));
        String line = new String();
        
        Set<String> urlSet = new HashSet<String>();
        List<Integer> infoboxAttrNum = new ArrayList<Integer>();
        Set<String> infoboxAttrSet = new HashSet<String>();
        int infoboxValHasUrlCount = 0;
        int outlineCount = 0, lev1 = 0, lev2 = 0;
        int h1 = 0, h2 = 0;
        int synonymCount = 0;
        int pageHasPolysemeCount = 0; // One page one time.
        int descriptionCount = 0;
        int contentCount = 0;
        List<Integer> imagesNum = new ArrayList<Integer>();
        List<Integer> tagsNum = new ArrayList<Integer>();
        Set<String> tagsSet = new HashSet<String>();
        Set<String> tagsUrlSet = new HashSet<String>();
        List<Integer> linksNum = new ArrayList<Integer>();
        Set<String> linksSet = new HashSet<String>();
        List<Integer> referencesNum = new ArrayList<Integer>();
        int statisticsCount = 0;

        int pageCount = 0;        
        while (true) {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            pageCount += 1;
            JSONObject page = new JSONObject(line);
            
            urlSet.add(page.getString("url"));

            JSONObject infobox = page.getJSONObject("infobox");
            infoboxAttrNum.add(infobox.length());
            infoboxAttrSet.addAll(infobox.keySet());
            for (String key : infobox.keySet()) {
            	if (infobox.getString(key).indexOf("[[")!=-1) {
            		infoboxValHasUrlCount += 1;
            	}
            }
            
            JSONObject outline = page.getJSONObject("outline");
            if (outline.length()!=0) {
            	outlineCount += 1;
	            lev1 += outline.length();
	            for (String key : outline.keySet()) {
	            	lev2 += outline.getJSONObject(key).length();
	            }
            }            
            
            JSONObject title = page.getJSONObject("title");
            if (title.length()!=0) {
            	if (title.getString("h1").equals("")==false) {
            		h1 += 1;
            	}
            	if (title.getString("h2").equals("")==false) {
            		h2 += 1;
            	}
            }
            
            JSONObject synonym = page.getJSONObject("synonym");
            if (synonym.length()!=0) {
            	synonymCount += 1;
            }
            
            JSONArray polyseme = page.getJSONArray("polyseme");
            if (polyseme.length()!=0) {
            	pageHasPolysemeCount += 1;
            }
            
            String description = page.getString("description");
            if (description.equals("")==false) {
            	descriptionCount += 1;
            }
            
            String content = page.getString("content");
            if (content.equals("")==false) {
            	contentCount += 1;
            }
            
            JSONArray images = page.getJSONArray("images");
        	imagesNum.add(images.length());
            
            JSONArray tags = page.getJSONArray("tags");
            tagsNum.add(tags.length());
            for (int i = 0; i < tags.length(); i += 1) {
                String tag = tags.getString(i);
                if (tag.indexOf("[[") != -1) {
                	tagsUrlSet.add(tag.split("\\|\\|")[1].split("\\]\\]")[0]);
                	tag = tag.split("\\|\\|")[0].split("\\[\\[")[1];
                }
                tagsSet.add(tag);
            }
           
            JSONArray links = page.getJSONArray("links");
            linksNum.add(links.length());
            links.forEach((link) -> linksSet.add(link.toString()));
                        
            JSONArray references = page.getJSONArray("references");
            referencesNum.add(references.length());
            
            JSONObject statistics = page.getJSONObject("statistics");
            if (statistics.length()!=0) {
            	statisticsCount += 1;
            }
            
            if (pageCount%100000==0) {
            	System.out.println("___" + pageCount);
            }
        }
        bufferedReader.close();
    	
        Collections.sort(infoboxAttrNum);
        Collections.sort(imagesNum);
        Collections.sort(tagsNum);
        Collections.sort(linksNum);
        Collections.sort(referencesNum);

        System.out.println("_______________Statistics_______________");
        System.out.println("urlSet\t" + urlSet.size());
//        System.out.println(infoboxAttrNum);
//        System.out.println(infoboxAttrSet);
        System.out.println("pageHasInfoboxAttrNum\t" + (infoboxAttrNum.size()-infoboxAttrNum.lastIndexOf(0)-1));
        System.out.println("infoboxAttrNum\t" + infoboxAttrNum.stream().mapToInt(Integer::intValue).sum());
        System.out.println("infoboxAttrSet\t" + infoboxAttrSet.size());
        System.out.println("infoboxValHasUrlCount\t" + infoboxValHasUrlCount);
        System.out.println("outlineCount\t" + outlineCount);
		System.out.println("lev1\t" + lev1);
		System.out.println("lev2\t" + lev2);
        System.out.println("h1\t" + h1);
        System.out.println("h2\t" + h2);
        System.out.println("synonymCount\t" + synonymCount);
        System.out.println("pageHasPolysemeCount" + pageHasPolysemeCount);
        System.out.println("descriptionCount\t" + descriptionCount);
        System.out.println("contentCount\t" + contentCount);
        System.out.println("pageHasImagesNum\t" + (imagesNum.size()-imagesNum.lastIndexOf(0)-1));
        System.out.println("imagesNum\t" + imagesNum.stream().mapToInt(Integer::intValue).sum());
        System.out.println("pageHasTagsNum\t" + (tagsNum.size()-tagsNum.lastIndexOf(0)-1));
//        System.out.println(tagsSet);
        System.out.println("tagsNum\t" + tagsNum.stream().mapToInt(Integer::intValue).sum());
        System.out.println("tagsSet\t" + tagsSet.size());
        System.out.println("tagHasUrlCount\t" + tagsUrlSet.size());
        System.out.println("pageHasLinksNum\t" + (linksNum.size()-linksNum.lastIndexOf(0)-1));
        System.out.println("linksNum\t" + linksNum.stream().mapToInt(Integer::intValue).sum());
        System.out.println("linksSet\t" + linksSet.size());
        System.out.println("pageHasReferencesNum\t" + (referencesNum.size()-referencesNum.lastIndexOf(0)-1));
        System.out.println("referencesNum\t" + referencesNum.stream().mapToInt(Integer::intValue).sum());
        System.out.println("statisticsCount\t" + statisticsCount);
        System.out.println("pageCount\t" + pageCount);
        System.out.println("_______________End_______________");
    }
    
    public void getDumpFile(String sourceFile, String resultFile) throws Exception {
    	BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(sourceFile)));
    	BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(resultFile)));
    	int pageCount = 0;
    	String line = new String();
        while (true) {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            pageCount += 1;
            JSONObject page = new JSONObject(line);
            List<String> sList = new ArrayList<String>();
            
            bufferedWriter.write("Title: " + page.getJSONObject("title").getString("h1") + "#####" + page.getJSONObject("title").getString("h2") + "\n");
            bufferedWriter.write("URL: " + page.getString("url") + "\n");
            if (page.getString("description").equals("") == false) {
            	bufferedWriter.write("Summary: " + page.getString("description") + "\n");
            }
            if (page.getJSONObject("infobox").length() != 0) {
            	bufferedWriter.write("Infobox: " + page.getJSONObject("infobox").toString() + "\n");
            }
            
//            bufferedWriter.write("Category: " + "" + "\n");
            
            sList.clear();
            page.getJSONArray("links").forEach((l) -> sList.add(l.toString()));
            if (sList.size()!=0) {
            	bufferedWriter.write("Innerlink: " + StringUtils.join(sList, "::;") + "\n");
            }
            sList.clear();
            page.getJSONArray("images").forEach((i) -> sList.add(i.toString()));
            String i1 = "", i2 = "";
            if (sList.size() >= 1) {
            	i1 = sList.get(0);
            	i2 = StringUtils.join(sList.subList(1, sList.size()), "::;");
            }
            if (sList.size() >= 1) {
            	bufferedWriter.write("FirstImage: " + i1 + "\n");
            }
            if (sList.size() > 1) {
            	bufferedWriter.write("Images: " + i2 + "\n");
            }
            if (page.getString("content").equals("") == false) {
            	bufferedWriter.write("FullText: " + page.getString("content") + "\n");
            }
            if (page.getJSONObject("outline").length() != 0) {
            	bufferedWriter.write("Outline: " + page.getJSONObject("outline").toString() + "\n");
            }
            sList.clear();
            page.getJSONArray("references").forEach((r) -> sList.add(r.toString()));
            if (sList.size() != 0) {
            	bufferedWriter.write("ExternalLink: " + StringUtils.join(sList, "::;") + "\n");
            }
            if (page.getJSONObject("synonym").length() != 0) {
            	bufferedWriter.write("Synonym: " + page.getJSONObject("synonym").toString() + "\n");
            }
            if (page.getJSONObject("statistics").length() != 0) {
            	bufferedWriter.write("Statistics: " + page.getJSONObject("statistics").toString() + "\n");
            }
            sList.clear();
            page.getJSONArray("polyseme").forEach((p) -> sList.add(p.toString()));
            if (sList.size() != 0) {
            	bufferedWriter.write("Polyseme: " + StringUtils.join(sList, "::;") + "\n");
            }
            sList.clear();
            page.getJSONArray("tags").forEach((t) -> sList.add(t.toString()));
            if (sList.size() != 0) {
            	bufferedWriter.write("Tags: " + StringUtils.join(sList, "::;") + "\n");
            }
            sList.clear();
            page.getJSONArray("category").forEach((t) -> sList.add(t.toString()));
            if (sList.size() != 0) {
            	bufferedWriter.write("Category: " + StringUtils.join(sList, "::;") + "\n");
            }
            bufferedWriter.write("\n");
            
            if (pageCount%100000 == 0) {
        		System.out.println("___" + pageCount);
        	}
        }
        bufferedReader.close();
        bufferedWriter.close();
    }
    
    public void getSeparateAttrs(String sourceFile, String resultDir) throws Exception {
    	BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(sourceFile)));
    	String outputDir = resultDir;
    	BufferedWriter bufferedWriter_baidu_abstract = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-abstract.dat")));
//    	BufferedWriter bufferedWriter_baidu_tagList_all = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-tagList-all.dat")));
    	BufferedWriter bufferedWriter_baidu_innerLink = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-innerLink.dat")));
//    	BufferedWriter bufferedWriter_baidu_instance_tag = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-instance-tag.dat")));
    	BufferedWriter bufferedWriter_baidu_instanceList_all = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-instanceList-all.dat")));
    	BufferedWriter bufferedWriter_baidu_instanceof = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-instanceof.dat")));
    	BufferedWriter bufferedWriter_baidu_propertyList_all = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-propertyList-all.dat")));
    	BufferedWriter bufferedWriter_baidu_subclassof = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-subclassof.dat")));
    	BufferedWriter bufferedWriter_baidu_title_property = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-title-property.dat")));
    	BufferedWriter bufferedWriter_baidu_url = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-url.dat")));
    	BufferedWriter bufferedWriter_baidu_synonym = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-synonym.dat")));
    	BufferedWriter bufferedWriter_baidu_instance_concept = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-instance-concept.dat")));
    	BufferedWriter bufferedWriter_baidu_conceptListAll_all = new BufferedWriter(new FileWriter(new File(outputDir + "baidu-conceptList-all.dat")));

    	int pageCount = 0;
    	String line = new String();
    	Set<String> tagListAll = new HashSet<String>();
    	Set<String> conceptListAll = new HashSet<String>();
    	Set<String> propertyListAll = new HashSet<String>();
        while (true) {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            pageCount += 1;
            List<String> sList = new ArrayList<String>();
            JSONObject page = new JSONObject(line);
            JSONObject title = page.getJSONObject("title");
            String key = title.getString("h1") + "#####" + title.getString("h2");
            
            bufferedWriter_baidu_abstract.write(key + "\t" + page.getString("description").replaceAll("\t", ""));
            
            JSONArray links = page.getJSONArray("links");
            sList.clear();
            for (int i = 0; i < links.length(); i += 1) {
            	sList.add(links.get(i).toString());
            }
            bufferedWriter_baidu_innerLink.write(key + "\t" + StringUtils.join(sList, "::;") + "\n");
            
            JSONArray tags = page.getJSONArray("tags");
            sList.clear();
            for (int i = 0; i < tags.length(); i += 1) {
            	sList.add(tags.get(i).toString());
            	tagListAll.add(tags.get(i).toString());
            }
//            bufferedWriter_baidu_instance_tag.write(key + "\t" + StringUtils.join(sList, ";") + "\n");
            
            JSONArray category = page.getJSONArray("category");
            sList.clear();
            for (int i = 0; i < category.length(); i += 1) {
            	sList.add(category.get(i).toString());
            	conceptListAll.add(category.get(i).toString());
            }
            bufferedWriter_baidu_instance_concept.write(key + "\t" + StringUtils.join(sList, ";") + "\n");
            
        	bufferedWriter_baidu_instanceList_all.write(key + "\n");
        	
//        	bufferedWriter_baidu_instanceof
//        	bufferedWriter_baidu_subclassof
        	
        	JSONObject infobox = page.getJSONObject("infobox");
        	sList.clear();
        	for (String k : infobox.keySet()) {
        		sList.add(k + ":::" + infobox.getString(k));
        		propertyListAll.add(k);
        	}
        	bufferedWriter_baidu_title_property.write(key + "\t" + StringUtils.join(sList, "::;") + "\n");
        	        	
        	bufferedWriter_baidu_url.write(key + "\t" + page.getString("url") + "\n");
        	
        	JSONObject synonym = page.getJSONObject("synonym");
        	sList.clear();
        	if (synonym.length() != 0) {
        		String[] froms = synonym.getString("from").split("\\|\\|");
        		for (String sys : froms) {
        			sList.add(sys);
        		}
        	}
        	bufferedWriter_baidu_synonym.write(key + "\t" + StringUtils.join(sList, "::;") + "\n");       	
        	
        	if (pageCount%100000 == 0) {
        		System.out.println("___" + pageCount);
        	}
        }
//        for (String s : tagListAll) {
//        	bufferedWriter_baidu_tagList_all.write(s + "\n");
//        }
        for (String s : conceptListAll) {
        	bufferedWriter_baidu_conceptListAll_all.write(s + "\n");
        }
        for (String s : propertyListAll) {
        	bufferedWriter_baidu_propertyList_all.write(s + "\n");
        }
        bufferedReader.close();
        bufferedWriter_baidu_abstract.close();
//    	bufferedWriter_baidu_tagList_all.close();
    	bufferedWriter_baidu_conceptListAll_all.close();
    	bufferedWriter_baidu_innerLink.close();
//    	bufferedWriter_baidu_instance_tag.close();
    	bufferedWriter_baidu_instanceList_all.close();
    	bufferedWriter_baidu_instanceof.close();
    	bufferedWriter_baidu_propertyList_all.close();
    	bufferedWriter_baidu_subclassof.close();
    	bufferedWriter_baidu_title_property.close();
    	bufferedWriter_baidu_url.close();
    	bufferedWriter_baidu_instance_concept.close();
    	bufferedWriter_baidu_conceptListAll_all.close();

    }
    
    private LinkedHashMap<String, Integer> sortMap(Map<String, Integer> map, boolean HtoL){
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        List<Entry<String, Integer>> mappingList = new ArrayList<Entry<String, Integer>>(map.entrySet()); 
        
        Collections.sort(mappingList, new Comparator<Map.Entry<String, Integer>>(){ 
            public int compare(Map.Entry<String, Integer> map1, Map.Entry<String, Integer> map2){ 
                return HtoL ? map2.getValue().compareTo(map1.getValue()) : map1.getValue().compareTo(map2.getValue()); // High -> Low
            } 
        }); 
        
        for(Map.Entry<String, Integer> mapping : mappingList){
            sortedMap.put(mapping.getKey(), mapping.getValue());
        }
        return sortedMap;
    }
    public void forConcept() throws Exception {
        LinkedHashMap<String, Integer> conceptMap = new LinkedHashMap<String, Integer>();
        File sD = new File("/home/peter/BaiduBaikeDataProcess/vResult");
        
        for (File file : sD.listFiles()) {
            System.out.println(file.getName());
            File input = new File(file.getAbsolutePath());
            BufferedReader bufferedReader = new BufferedReader(new FileReader(input));
            String line = new String();
            while (true) {
                line = bufferedReader.readLine();
//                System.out.println(line);
                if (line == null || line.equals("")) {
                    break;
                }
                JSONObject page = new JSONObject(line);
                
                JSONArray tags = page.getJSONArray("tags");
                
                for (int i = 0; i < tags.length(); i += 1) {
                    String tag = tags.getString(i);
                    if (tag.indexOf("[[") != -1) {
                        tag = tag.split("\\|\\|")[0].split("\\[\\[")[1];
                    }
                    if (conceptMap.containsKey(tag)) {
                        conceptMap.put(tag, conceptMap.get(tag)+1);
                    } else {
                        conceptMap.put(tag, 1);
                    }
                }
//                break;
            }
            bufferedReader.close();
            System.out.println(sortMap(conceptMap, true));
        }
    }

    public void forCateInfobox() throws Exception {
        String sourceFile = "/home/peter/BaiduBaikeDataProcess/nResult/00000.nres.json";
        
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(sourceFile)));
        BufferedWriter bufferedWirter = new BufferedWriter(new FileWriter(new File(sourceFile.replace(".nres.", ".ncres."))));
        String line = new String();
        
        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};  
        
        while (true) {
            line = bufferedReader.readLine();
            if (line == null || line.equals("")) {
                break;
            }
            JSONObject page = new JSONObject(line);
            
            JSONObject outline = page.getJSONObject("outline");
            String text_content = page.getString("text_content");
            JSONObject cate_infobox = new JSONObject();
            
            if (outline.length() > 0) {
                HashMap<String, Integer> map = new HashMap<String, Integer>();
                String keyReg = new String();
                for (String key : outline.keySet()) {
                    map.put(key, Integer.parseInt(key.split("~")[0]));
                    String tem_s = key.substring(key.indexOf("~")+1);
                    for (String k : fbsArr) {
                        if (tem_s.contains(k)) {
                            tem_s = tem_s.replace(k, "\\"+k);
                        }
                    }
                    keyReg += "|" + "~~~~~" + tem_s;
                }
                
                LinkedHashMap<String, Integer> sortedMap = sortMap(map, false);
                String[] parasWithinNoise = text_content.split(keyReg.substring(1));
                ArrayList<String> paras = new ArrayList<String>();
                for (int i = 0; i < parasWithinNoise.length; i += 1) {
                    if (parasWithinNoise[i].equals("")==false) {
                        paras.add(parasWithinNoise[i]);
                    }
                }
                if (paras.size() == sortedMap.size()) {
//                    System.out.println("...");
                    for (String key : sortedMap.keySet()) {
                        cate_infobox.put(key, paras.get(sortedMap.get(key)-1));
                        if (outline.getJSONObject(key).length() > 0) {
                            //TODO: Add when you have <h3>
                        }
                    }
                } else {
//                    System.out.println("warning...");
                }
            }
            page.put("cate_infobox", cate_infobox);
            bufferedWirter.write(page.toString() + "\n");
        }
        bufferedReader.close();
        bufferedWirter.close();
    }

    public void addCategory(String sourceFile, String categoryFile, String resultFile) throws Exception {
    	BufferedReader bufferedReader_json = new BufferedReader(new FileReader(new File(sourceFile)));
    	BufferedReader bufferedReader_cate = new BufferedReader(new FileReader(new File(categoryFile)));
    	BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(resultFile)));
    	int cnt = 0;
    	String line = new String();
    	Map<String, JSONArray> cate = new HashMap<String, JSONArray>();
    	while (true) {
    		line = bufferedReader_cate.readLine();
            if (line == null) {
                break;
            }
            cnt += 1;
            String[] key_val = line.split("\t");
            cate.put(key_val[0], new JSONArray(key_val[1].split(";")));
    	}
    	System.out.println("Cate len:" + cate.size());
    	cnt = 0;
        while (true) {
            line = bufferedReader_json.readLine();
            if (line == null) {
                break;
            }
            cnt += 1;
            JSONObject page = new JSONObject(line);
            JSONObject title = page.getJSONObject("title");
            String key = title.getString("h1") + "#####" + title.getString("h2");
            if (cate.containsKey(key)) {
            	page.put("category", cate.get(key));
            } else {
            	page.put("category", new JSONArray());
            }
        	bufferedWriter.write(page.toString() + "\n");
            if (cnt%100000==0) {
            	System.out.println("___" + cnt);
            }
        }
        bufferedReader_json.close();
        bufferedReader_cate.close();
        bufferedWriter.close();
    }
        
}
