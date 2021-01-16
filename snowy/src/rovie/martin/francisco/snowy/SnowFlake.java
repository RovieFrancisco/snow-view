package rovie.martin.francisco.snowy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

class SnowFlake {
  private static final float ANGE_RANGE = 0.1f;
  private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f;
  private static final float HALF_PI = (float) Math.PI / 2f;
  private static final float ANGLE_SEED = 25f;
  private static final float ANGLE_DIVISOR = 10000f;
  private static final float INCREMENT_LOWER = 2.0f; //0.5
  private static final float INCREMENT_UPPER = 4.0f; //1.5
  private static final float FLAKE_SIZE_LOWER = 5f;
  private static final float FLAKE_SIZE_UPPER = 40f; //17

  private final Random random;
  private final PointF position;
  private float angle;
  private final float increment;
  private final float flakeSize;
  private final Paint paint;
  private Bitmap snowBitmap;
  private RectF desRect = new RectF();
  private int rotateAngle = 0;
  Matrix matrix = new Matrix();
  private int paintAlpha = 255;

  public static SnowFlake create(Bitmap snowBitmap, int width, int height, Paint paint) {
    Random random = new Random();
    float flakeSize = random.getRandom(FLAKE_SIZE_LOWER, FLAKE_SIZE_UPPER);
    float x = (float) random.getRandom(width);//(flakeSize, width - flakeSize);
    float y = (float) random.getRandom(height);//(-flakeSize, height + flakeSize);
    PointF position = new PointF(x, y);
    float angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
    float increment = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER);

    return new SnowFlake(snowBitmap, random, position, angle, increment, flakeSize, paint);
  }

  SnowFlake(Bitmap snowBitmap, Random random, PointF position, float angle, float increment, float flakeSize, Paint paint) {
    this.random = random;
    this.position = position;
    this.angle = angle;
    this.increment = increment;
    this.flakeSize = flakeSize;
    this.paint = paint;
    this.snowBitmap = snowBitmap;
    //boolean randomBoolean = random.getRandomBoolean();
    if (random.getRandomBoolean()) {
      paintAlpha = 50; //51
    } else {
      paintAlpha = random.getRandom(255); //128
    }
  }

  private void move(int width, int height) {
    float x = (float) (position.x + increment * Math.cos(angle));
    float y = (float) (position.y + increment * Math.sin(angle));

    angle += random.getRandom(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR;
    rotateAngle += angle;
    rotateAngle %= 360;
    position.set(x, y);
    desRect.set((float) (x - flakeSize), (float) (y - flakeSize), (float) (x + flakeSize), (float) (y + flakeSize));

    if (!isInside(width, height)) {
      reset(width);
    }
  }

  private boolean isInside(int width, int height) {
    float x = position.x;
    float y = position.y;
   // return x >= -flakeSize - 1 && x + flakeSize <= width && y >= -flakeSize - 1 && y - flakeSize < height;
    return (x + flakeSize > 1) && (x - flakeSize < width) && (y - flakeSize < height) && (y >= -flakeSize - 1);
  }

  private void reset(int width) {
    position.x = random.getRandom(width);
    position.y = (int) (-flakeSize - 1);
   // angle = random.getRandom(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
    angle = (((random.getRandom(ANGLE_SEED) / ANGLE_SEED) * ANGE_RANGE) + HALF_PI) - HALF_ANGLE_RANGE;
  }

  public void draw(Canvas canvas) {
    int width = canvas.getWidth();
    int height = canvas.getHeight();

    if (width <= 0 || height <= 0) {
      return;
    }

    move(width, height);
    if (snowBitmap != null) {
      float scaleValue = (flakeSize * 2) / (snowBitmap.getHeight());
      matrix.reset();
      matrix.postScale(scaleValue, scaleValue);
      matrix.postRotate(rotateAngle);
      matrix.postTranslate(position.x, position.y);
      paint.setAlpha(paintAlpha);
      canvas.drawBitmap(snowBitmap, matrix, paint);
/**
      canvas.save();
      canvas.drawBitmap(snowBitmap, null, desRect, paint);
      canvas.restore();
**/
    } else {
      canvas.drawCircle(position.x, position.y, flakeSize, paint);
    }
  }
}
