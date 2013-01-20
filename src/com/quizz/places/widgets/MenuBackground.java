package com.quizz.places.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.view.View;

public class MenuBackground extends View {

    public float rotationDegrees = 0f;
    private Point mCenterPoint = new Point();
    private Point mPointB = new Point();
    private Point mPointC = new Point();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init() {

	mPaint.setStrokeWidth(2);
	mPaint.setColor(Color.parseColor("#33FFB90F"));
	mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	mPaint.setAntiAlias(true);
    }

    public MenuBackground(Context context) {
	super(context);
	init();
    }

    public MenuBackground(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public MenuBackground(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	canvas.rotate(rotationDegrees, mCenterPoint.x, mCenterPoint.y);
    }

    private void drawFoggyWindowWithTransparentCircle(Canvas canvas, Bitmap bitmap, float circleX,
	    float circleY, float radius) {

	// Create a temporary bitmap
	Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
		Bitmap.Config.ARGB_8888);
	Canvas tempCanvas = new Canvas(tempBitmap);

	// Copy bitmap into tempBitmap
	tempCanvas.drawBitmap(bitmap, 0, 0, null);

	// Create a radial gradient
	RadialGradient gradient = new android.graphics.RadialGradient(circleX, circleY, radius,
		0xFF000000, 0x00000000, android.graphics.Shader.TileMode.CLAMP);

	// Draw transparent circle into tempBitmap
	Paint p = new Paint();
	p.setShader(gradient);
	p.setColor(0xFF000000);
	p.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
	tempCanvas.drawCircle(circleX, circleY, radius, p);

	// Draw tempBitmap onto the screen (over what's already there)
	canvas.drawBitmap(tempBitmap, 0, 0, null);
    }

    @Override
    public void draw(Canvas canvas) {
	super.draw(canvas);

	// angle lumineux : 20
	// angle ecart : 40

	mCenterPoint.x = getWidth() / 2;
	mCenterPoint.y = (int) (getHeight() * 0.3f);

	Bitmap bitmap = Bitmap.createBitmap((int) getWidth(), (int) getHeight(), Config.RGB_565);
	Canvas canvasTmp = new Canvas(bitmap);

	int distance = (int) Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2));
	for (int angle = 0; angle < 360; angle += 60) {

	    double thetaB = Math.toRadians(angle);
	    mPointB.x = (int) (mCenterPoint.x + distance * Math.cos(thetaB));
	    mPointB.y = (int) (mCenterPoint.y + distance * Math.sin(thetaB));

	    double thetaC = Math.toRadians(angle + 20);
	    mPointC.x = (int) (mCenterPoint.x + distance * Math.cos(thetaC));
	    mPointC.y = (int) (mCenterPoint.y + distance * Math.sin(thetaC));

	    Path path = new Path();
	    path.setFillType(Path.FillType.EVEN_ODD);
	    path.moveTo(mCenterPoint.x, mCenterPoint.y);
	    path.lineTo(mPointB.x, mPointB.y);
	    path.lineTo(mPointC.x, mPointC.y);
	    path.close();

	    thetaB = Math.toRadians(angle + 20);
	    mPointB.x = (int) (mCenterPoint.x + distance * Math.cos(thetaB));
	    mPointB.y = (int) (mCenterPoint.y + distance * Math.sin(thetaB));

	    thetaC = Math.toRadians(angle + 60);
	    mPointC.x = (int) (mCenterPoint.x + distance * Math.cos(thetaC));
	    mPointC.y = (int) (mCenterPoint.y + distance * Math.sin(thetaC));

	    canvasTmp.drawPath(path, mPaint);

	    mCenterPoint.x = getWidth() / 2;
	    mCenterPoint.y = (int) (getHeight() * 0.3f);
	    drawFoggyWindowWithTransparentCircle(canvas, bitmap, mCenterPoint.x, mCenterPoint.y,
		    getWidth() / 1.6f);
	}
    }
}
