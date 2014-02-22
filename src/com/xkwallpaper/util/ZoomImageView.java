package com.xkwallpaper.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
/**
 * 支持多点触摸可缩放的图片组件，提供滑动边界判断解决方案
 * @author YPF
 *
 */
public class ZoomImageView extends ImageView {

	
    private int dWidth;// 屏幕宽度
    private int dHeight;

    private float bWidth;// 图片宽度
    private float bHeight;
    float initScale = 0;
    float mScale = 0;
    private float scale = 0;
    /**图片当前状态**/
    private Matrix mMatrix;
    /**图片初始状态**/
    private Matrix initMatrix;
    /**图片上一个状态**/
    private Matrix mSavedMatrix;
    //图片第一次显示的初始长宽
    float initWidth = 0;
    float initHeight = 0;
    //图片第一次显示的四边位置
    float initLeft = 0;
    float initRight = 0;
    float initTop = 0;
    float initBottom = 0;
    //范围存储
    private ImageState mapState = new ImageState();
    //变化存储
    private float[] values = new float[9];
    //起点位置
    private PointF mStart = new PointF();
    /*图片真正的长宽*/
   private float bitMapWidth = 0;
   private float bitMapHeight = 0;
   //图片可放大的比率
   private int maxEnlargeScale = 3;
   //操作值
   private String zoomMode = null;
   private float oldDist = 0;
   //手势判断
   private GestureDetector gd;
   //控制双击事件，双击恢复初始
   private boolean mMark = false; 
   //图片数据流
   private Bitmap mBitmap = null;
   //多点触摸判断
   private boolean mIsZoom = false;
   //修改值
   private float modifyValue = 0;
   /*起始点*/
   private float mStartX = 0;
   private float mStartY = 0;
	public int zoom = 1;
	private Context mContext;
	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setScaleType(ScaleType.MATRIX);
		this.mContext=context;
	}
	public ZoomImageView(Context context){
		super(context);
		this.setScaleType(ScaleType.MATRIX);
		this.mContext=context;
	}
    /**
     * 获取移动距离
     * @return 
     */
    public float getOldDist(MotionEvent event) {
        this.oldDist = this.spacing(event);
        if (oldDist > 10f) {
            mSavedMatrix.set(mMatrix);
        }
        return oldDist;
    }
    /**
     * 初始化图片数据
     * @param event
     */
    public void init(MotionEvent event){
        mSavedMatrix.set(mMatrix);
        mStart.set(event.getX(), event.getY());
    }
    /**
     * 判断是否到了右边界
     * @return
     */
    public boolean getNext(){
        return mapState.right <= dWidth && mapState.left <= -2;
     }
    /**
     *判断是否到了左边界
     * @return
     */
     public boolean getBack(){
         return mapState.left >= 0 && mapState.right >= dWidth;
     }
     /**
      * 设置屏幕大小
      * @param width
      * @param height
      * @param bitmap
      */
     public void setScreenSize(int width, int height,Bitmap bitmap) {
         mBitmap =bitmap;
         super.setImageBitmap(mBitmap);
         dWidth = width;
         dHeight = height;
         initScale = scale;

         bWidth = mBitmap.getWidth();
         bHeight = mBitmap.getHeight();
         
         float xScale = (float) dWidth / bWidth;
         float yScale = (float) dHeight / bHeight;
         mScale = xScale <= yScale ? xScale : yScale;
         scale = mScale < 1 ? mScale : 1;
         initScale = scale;
         mMatrix = new Matrix();
         initMatrix = new Matrix();

         mSavedMatrix = new Matrix();
         // 平移
         mMatrix.postTranslate((dWidth - bWidth) / 2, (dHeight - bHeight) / 2);

         float sX = dWidth / 2;
         float sY = dHeight / 2;
         initWidth = bWidth * scale;
         initHeight = bHeight * scale;

         mMatrix.postScale(scale, scale, sX, sY);
         initMatrix.set(mMatrix);
         mSavedMatrix.set(mMatrix);
         setView();
     }
     /**
      * 刷新图片界面
      */
     public void setView() {
         this.setImageMatrix(mMatrix);
         Rect rect = this.getDrawable().getBounds();
         this.getImageMatrix().getValues(values);
         bWidth = rect.width() * values[0];
         bHeight = rect.height() * values[0];

         mapState.left = values[2];
         mapState.top = values[5];
         mapState.right = mapState.left + bWidth;
         mapState.bottom = mapState.top + bHeight;
     }
   /**
    * 缩放图片
    * @param event
    */
     public void zoom(MotionEvent event) {
         float newDist = spacing(event);
         if (newDist > 10f) {
             mMatrix.set(mSavedMatrix);
             scale = newDist / oldDist;
             // 缩放模式为缩小
             if (scale < 1) {
                 zoomMode = "small";
                 mMatrix.postScale(scale, scale, dWidth / 2, dHeight / 2);
             } else {// 缩放模式为放大
                 zoomMode = "enlarge";
                 mMatrix.postScale(scale, scale, dWidth / 2, dHeight / 2);

             }

         }
         zoom = 2;
     }
    /**
     * 拖动
     * @param event
     */
     public void drag(MotionEvent event){
         mMatrix.set(mSavedMatrix);
         // 上下左右都至少有边界出界时，能随意拖动
         if ((mapState.left <= 0 || mapState.right >= dWidth)
                 && (mapState.top <= 0 || mapState.bottom >= dHeight)) {
             mMatrix.postTranslate(event.getX() - mStart.x, event.getY()
                     - mStart.y);
       // 当只有上下一方出界，只能上下拖动
         } else if (mapState.top <= 0 || mapState.bottom >= dHeight) {
             mMatrix.postTranslate(0, event.getY() - mStart.y);
             // 当只有左右一方出界时，只能左右拖动
         } else if (mapState.left <= 0 || mapState.right >= dWidth) {
             mMatrix.postTranslate(event.getX() - mStart.x, 0);
         }
     }
     /**
      * 回弹
      * @param mode
      */
     public void up(int mode){
         // 当图片脱离左边界且图片右边界大于屏幕右边时，则弹到最左边或跳到上面
            if (mapState.left >= 0 && mapState.right >= dWidth) {
                if (bWidth > dWidth) {
                    mMatrix.postTranslate(0 - mapState.left, 0);
                } else {
                    mMatrix.set(initMatrix);
                }
            }
            // 当图片脱离右边界时，则弹到最右边或跳到下面
            if (mapState.right <= dWidth && mapState.left <= 0) {
                if (bWidth > dWidth) {
                    mMatrix.postTranslate(dWidth - mapState.right, 0);
                } else {
                    mMatrix.set(initMatrix);
                }
            }

            // 当图片脱离上边界时，则弹到最上边
            if (mapState.top >= 0 && mapState.bottom >= dHeight) {
                mMatrix.postTranslate(0, 0 - mapState.top);
            }
            // 当图片脱离下边界时，则弹到最下边，增加修正范围
            if (mapState.bottom + modifyValue <= dHeight && mapState.top <= 0) {
                mMatrix.postTranslate(0, dHeight - mapState.bottom
                        - modifyValue);

            }

            // 若为缩放模式
            if (mode == 2) {
                // 当图片长宽都小于屏大小时，则图片大小弹为初始大小
                setView();
                if ((bWidth < initWidth) && (bHeight < initHeight)) {
                	zoom = 1;
                    mMatrix.set(initMatrix);
                }
                // 当图片有X\Y两个方向都至少有边界脱离屏幕
                if ((mapState.left >= initLeft || mapState.right <= initRight)
                        && (mapState.top >= initTop || mapState.bottom <= initBottom)) {
                    // 且为缩小模式时，则图片大小弹为初始大小
                    if ("small".equals(zoomMode)) {
                        mMatrix.set(initMatrix);
                    }
                }

            }
        }
     /**
      * 初始化图片
      */
     public void setInit(){
         mMatrix.set(initMatrix);
         this.setView();
     }
   /**
    * 计算移动距离
    * @param event
    * @return
    */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    private final int NONE = 0; // 无模
    private final int DRAG = 1; // 拖动模式
    private final int ZOOM = 2; // 缩放模式
    int mode = NONE;

    private float calculate(float x1,  float x2) {// 在点(x1,y1)和点(x2,y2)之间用当前颜色画�?��线段

        float pz = x1 - x2;// 计算两点间的距离
        return pz;
    }
	/**
	 * 用于保存图片范围的类
	 * @author YPF
	 *
	 */
    private class ImageState {
        private float left;
        private float top;
        private float right;
        private float bottom;
    }
    
    public void recycle(){
    	if(mBitmap != null && !mBitmap.isRecycled()){
    		mBitmap.recycle();
    	}
    }
    
    private String imageName;
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
    
}
