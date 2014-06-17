package edu.ksu.jsonparser.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    //url delete =cbfunc
         static public String yahooStockInfo = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22MSFT%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=cbfunc";
    static String stockSymbol = "";
    static String stockDaysLow = "";
    static String stockDaysHight = "";
    static String stockChange = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new MyAsyncTask().execute();

    }
    public class MyAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(yahooStockInfo);

            httpPost.setHeader("Content-type","application/json");
            InputStream inputStream = null;
            String result = null;
            try{
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"),8);
                StringBuilder theStringBuilder = new StringBuilder();
                String line= null;
                while((line=reader.readLine())!=null){
                    theStringBuilder.append(line+"\n");
                }
                result = theStringBuilder.toString();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try{
                    if(inputStream != null) inputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject;
            try{
result=result.substring(7);
                result = result.substring(0,result.length()-2);
               // Log.v("JSONParser RESULT",result);
                jsonObject = new JSONObject(result);
                JSONObject queryJSONObject = jsonObject.getJSONObject("query");
                JSONObject resultsJSONObject = queryJSONObject.getJSONObject("results");
                JSONObject quoteJSONObject = resultsJSONObject.getJSONObject("quote");

                stockSymbol = quoteJSONObject.getString("symbol");
                stockDaysLow = quoteJSONObject.getString("DaysLow");
                stockDaysHight = quoteJSONObject.getString("DaysHight");
                stockChange = quoteJSONObject.getString("Change");

                JSONArray queryArray = quoteJSONObject.names();
                List<String> list = new ArrayList<String>();
                for(int i=0;i<queryArray.length();i++){
                    list.add(queryArray.getString(i));
                }
                for(String item:list){
                    Log.v("JSON ARRAY ITEMS",item);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            TextView line1 = (TextView) findViewById(R.id.line1);
            TextView line2 = (TextView) findViewById(R.id.line2);
            TextView line3 = (TextView) findViewById(R.id.line3);

            line1.setText("Stock: "+stockSymbol+" : "+stockChange);
            line2.setText("Days Low: "+stockDaysLow);
            line3.setText("Days Hight: "+stockDaysHight);
        }
    }
}
