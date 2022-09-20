package de.brainschweig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadXmlDomParser {

    private static final String FILENAME = "Vocabulary.xml.not_so_old";

    public static void main(String[] args) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            HttpClient httpclient = HttpClients.createDefault();

            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(FILENAME));

            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("Vokabeln");

            for (int temp = 0; temp < list.getLength(); temp++) {
                HttpPost httppost = new HttpPost("http://127.0.0.1:9001/api/v1.0/vocabulary");

                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String chinesisch = element.getElementsByTagName("Chinesisch").item(0).getTextContent();
                    String pinyin = element.getElementsByTagName("Pinyin").item(0).getTextContent();
                    String german = element.getElementsByTagName("German").item(0).getTextContent();

                    System.out.println("Chinesisch: " + chinesisch);
                    System.out.println("Pinyin    : " + pinyin);
                    System.out.println("German    : " + german);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("hanze", chinesisch));
                    params.add(new BasicNameValuePair("pinyin", pinyin));
                    params.add(new BasicNameValuePair("german", german));
                    httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                }

            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

}