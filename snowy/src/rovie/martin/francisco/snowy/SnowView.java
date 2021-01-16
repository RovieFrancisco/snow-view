package rovie.martin.francisco.snowy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class SnowView extends View {
  private static final int NUM_SNOWFLAKES = 30; //15
  private static final int DELAY = 5; //10

  private SnowFlake[] snowflakes;
  private Bitmap snowBitmap;

  public SnowView(Context context) {
    super(context);
    initSnowBitmap();
  }

  public SnowView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initSnowBitmap();
  }

  public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initSnowBitmap();
  }

  private void initSnowBitmap() {
    try {
        snowBitmap = BitmapFactory.decodeStream(getContext().getAssets().open("snowFlakes.png"));
    } catch (Exception e) {}
  }

  public void setSnowBitmap(Bitmap SnowBitmap) {
    snowBitmap = SnowBitmap;
  }

  protected void resize(int width, int height) {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.FILL);

    snowflakes = new SnowFlake[NUM_SNOWFLAKES];
    for (int i = 0; i < NUM_SNOWFLAKES; i++) {
      snowflakes[i] = SnowFlake.create(snowBitmap, width, height, paint);
    }
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    super.onSizeChanged(width, height, oldWidth, oldHeight);

    if (width != oldWidth || height != oldHeight) {
      resize(width, height);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for (SnowFlake snowFlake : snowflakes) {
      snowFlake.draw(canvas);
    }

    handler.sendEmptyMessageDelayed(888, DELAY);
  }

  private Handler handler = new Handler() {
    @Override public void handleMessage(Message msg) {
      switch (msg.what) {
        case 888:
          handler.removeMessages(888);
          invalidate();
          break;
      }
    }
  };
}
