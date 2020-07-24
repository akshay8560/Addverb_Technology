package com.example.asiancountries;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CountryActivity extends AppCompatActivity {

    Country c;
    SVGImageView view;
    ProgressBar p;
    public class ImageDownload extends AsyncTask<String,Void, SVG>
    {
        @Override
        protected SVG doInBackground(String... strings) {

            try{
                URL url=new URL(strings[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                InputStream stream=connection.getInputStream();
                SVG file=SVG.getFromInputStream(stream);

                return file;
            }
            catch(MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            } catch (SVGParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(SVG bitmap) {
            super.onPostExecute(bitmap);

            p.setVisibility(View.GONE);
            view=findViewById(R.id.flag);
            view.setSVG(bitmap);
            Log.i("Image","Set");
        }
    }


    TextView capital,population,code,region,subRegion,borders,language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        ActionBar bar=getSupportActionBar();
        c = (Country) getIntent().getSerializableExtra("Country");
        bar.setTitle(c.getName());
        capital=findViewById(R.id.capital);
        population=findViewById(R.id.population);
        code=findViewById(R.id.code);
        region=findViewById(R.id.region);
        subRegion=findViewById(R.id.sub_region);

        borders=findViewById(R.id.borders);
        language=findViewById(R.id.languages);

        capital.setText("Capital: "+c.getCapital());
        if(c.getCapital().isEmpty())
            capital.setText("Capital: "+c.getName());
        population.setText("Population: "+String.valueOf(c.getPopulation()));
        code.setText("Country Code: " + c.getCode());
        region.setText("Region: " +c.getRegion());
        subRegion.setText("Sub Region: "+c.getSubRegion());
        String temp="";
        for(String s:c.getBorders())
            temp+=s+", ";
        if(temp.length()==0)
            borders.setText(c.getBorders().toString());
        else {
            temp = temp.substring(0, temp.length() - 2);
            borders.setText("Has Borders With " + temp);
        }
        temp="";
        for(String s:c.getLanguages())
            temp+=s+", ";
        temp=temp.substring(0,temp.length()-2);
        language.setText("Languages Spoken in this country are "+temp);


        view=findViewById(R.id.flag);
        p=findViewById(R.id.progressBar);
        ImageDownload download=new ImageDownload();
        Log.i("Flag URL",c.getFlag());
        download.execute(c.getFlag());
    }
}
