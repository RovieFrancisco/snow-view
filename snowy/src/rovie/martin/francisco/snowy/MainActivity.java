package rovie.martin.francisco.snowy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.content.Context;
import android.provider.MediaStore;
import android.graphics.BitmapFactory;
import android.content.pm.PackageManager;
import android.view.View;

public class MainActivity extends Activity {

    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    public String imgpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

        sp = getBaseContext().getSharedPreferences("SNOW", Context.MODE_PRIVATE);
        editor = sp.edit();

        SnowView sv = (SnowView) findViewById(R.id.snowView);
        sv.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "Swipe Up", 1).show();
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
            }
        });

        if(sp.contains("imagePath")) {
           sv.setSnowBitmap(BitmapFactory.decodeFile(sp.getString("imagePath", "")));
		      }
    }

    public void Pick(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK	&& null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.MediaColumns.DATA };
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgpath = cursor.getString(columnIndex);
                cursor.close();

                editor.putString("imagePath", imgpath).commit();

                Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                Intent mainIntent = Intent.makeRestartActivityTask(intent.getComponent());
                startActivity(mainIntent);
                Runtime.getRuntime().exit(0);
            }
        } catch (Exception e) { }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(MainActivity.this, "Access Granted", 1).show();
                else
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }
}