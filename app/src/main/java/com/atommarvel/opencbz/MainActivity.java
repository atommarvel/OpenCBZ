package com.atommarvel.opencbz;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        if (!isCBZCopied()) {
            copyCBZToExternalDirectory();
        }
    }

    private boolean isCBZCopied() {
        File file = new File(getCBZPath());
        return file.isFile();
    }

    private void copyCBZToExternalDirectory() {
        InputStream in = getResources().openRawResource(R.raw.example_comic);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getCBZPath());
            byte[] buff = new byte[1024];
            int read = 0;

            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "file copied to external directory", Toast.LENGTH_SHORT).show();
    }

    private String getCBZPath(){
        return getExternalFilesDir(null).getAbsolutePath() + "/example_comic.cbz";
    }

    public void openViaACR(View view) {
        String pkg = "com.aerilys.acr.android";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(pkg);
        intent.setType("application/x-cbz");
        intent.setData(Uri.parse(getCBZPath()));
        safelyLaunchIntent(intent);
    }

    public void openViaOCR(View view) {
        String pkg = "com.sketchpunk.ocomicreader";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(pkg);
        intent.setComponent(new ComponentName(pkg, "com.sketchpunk.ocomicreader.ViewActivity"));

        intent.setType("application/x-cbz");
        intent.setData(Uri.parse(getCBZPath()));
        safelyLaunchIntent(intent);
    }

    public void safelyLaunchIntent(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (activities.size() > 0) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Was not able to safely launch intent", Toast.LENGTH_SHORT).show();
        }
    }
}
