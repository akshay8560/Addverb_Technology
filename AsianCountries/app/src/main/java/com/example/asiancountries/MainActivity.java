package com.example.asiancountries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar tBar;
    ProgressBar stat;
    ArrayList<String> country;
    ArrayList<String> capital;
    ArrayList<Country> currentList;

    ArrayList<Country> allCountries;
    ListView countryList;

    CustomAdapter adap;



    public class LoadAPI extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            try{
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                InputStream iStream=connection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(iStream));
                StringBuilder sb=new StringBuilder();
                String temp="";
                while((temp=bufferedReader.readLine())!=null)
                {
                    sb.append(temp);
                }
                String apiData=sb.toString();
                Log.i("Image","Downloaded");
                return apiData;


            }
            catch(MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray array = new JSONArray(s);
                for(int i=0;i<array.length();i++)
                {
                    JSONObject tempObj=array.getJSONObject(i);
                    Country newC=new Country();
                    newC.setName(tempObj.getString("name"));
                    newC.setCapital(tempObj.getString("capital"));
                    newC.setCode(tempObj.getString("alpha3Code"));
                    newC.setFlag(tempObj.getString("flag"));
                    newC.setRegion(tempObj.getString("region"));
                    newC.setSubRegion(tempObj.getString("subregion"));
                    newC.setPopulation(tempObj.getDouble("population"));
                    ArrayList<String> tempList=new ArrayList<>();
                    JSONArray tempArr=tempObj.getJSONArray("borders");
                    for(int i1=0;i1<tempArr.length();i1++)
                    {
                        tempList.add(tempArr.getString(i1));
                    }
                    newC.setBorders(tempList);
                    tempList=new ArrayList<>();
                    tempArr=tempObj.getJSONArray("languages");
                    for (int i1=0;i1<tempArr.length();i1++)
                    {
                        JSONObject obj=tempArr.getJSONObject(i1);
                        tempList.add(obj.getString("name"));
                    }
                    newC.setLanguages(tempList);
                    allCountries.add(newC);
                    country.add(newC.getName());
                    capital.add(newC.getCapital());
                    currentList.add(newC);

                }
                for(int i=0;i<allCountries.size();i++)
                {
                    Country tempC=allCountries.get(i);
                    ArrayList<String> fullName=new ArrayList<>();
                    for(String c:tempC.getBorders())
                    {
                        for(int j=0;j<allCountries.size();j++)
                        {
                            if(c.equals(allCountries.get(j).getCode()))
                            {
                                fullName.add(allCountries.get(j).getName());
                                break;
                            }
                        }
                    }
                    tempC.setBorders(fullName);
                    allCountries.set(i,tempC);
                }
                adap=new CustomAdapter(MainActivity.this,country,capital);
                countryList.setAdapter(adap);
                assignOnClick();
                stat.setVisibility(View.GONE);
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }
    }


    public void assignOnClick()
    {
        countryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent inte=new Intent(getApplicationContext(),CountryActivity.class);
                inte.putExtra("Country",currentList.get(position));
                startActivity(inte);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stat=findViewById(R.id.stat);
        country=new ArrayList<>();
        capital=new ArrayList<>();
        allCountries=new ArrayList<>();
        currentList=new ArrayList<>();
        countryList=findViewById(R.id.countryList);
        LoadAPI api=new LoadAPI();
        String url="https://restcountries.eu/rest/v2/region/asia";
        api.execute(url);

    }

    public void loadUpRes(String s)
    {
        ArrayList<String> tempCo=new ArrayList<>();
        ArrayList<String> tempCa=new ArrayList<>();
        currentList.clear();
        for(Country c:allCountries)
        {
            if(c.getName().contains(s))
            {
                tempCo.add(c.getName());
                tempCa.add(c.getCapital());
                currentList.add(c);
            }
        }
        country=tempCo;
        capital=tempCa;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_bar_main_page,menu);
        MenuItem mItem=menu.findItem(R.id.searchButton);
        SearchView searchView =(SearchView) mItem.getActionView();
        searchView.setQueryHint("Enter name of the country");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadUpRes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadUpRes(newText);
                adap=new CustomAdapter(MainActivity.this,country,capital);
                countryList.setAdapter(adap);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
