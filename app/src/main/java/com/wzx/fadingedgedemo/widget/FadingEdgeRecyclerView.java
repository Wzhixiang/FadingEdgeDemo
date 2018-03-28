package com.wzx.fadingedgedemo.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.wzx.fadingedgedemo.R;

import static android.graphics.Canvas.ALL_SAVE_FLAG;

/**
 * 描述 TODO
 * Created by Administrator on 2018/3/28.
 */

public class FadingEdgeRecyclerView extends RecyclerView {

    private static final int DEFAULT_GRADIENT_SIZE_DP = 80;

    public static final int FADE_EDGE_TOP = 1;
    public static final int FADE_EDGE_BOTTOM = 2;
    public static final int FADE_EDGE_LEFT = 4;
    public static final int FADE_EDGE_RIGHT = 8;

    private static final int DIRTY_FLAG_TOP = 1;
    private static final int DIRTY_FLAG_BOTTOM = 2;
    private static final int DIRTY_FLAG_LEFT = 4;
    private static final int DIRTY_FLAG_RIGHT = 8;

    private static final int[] FADE_COLORS = new int[]{Color.TRANSPARENT, Color.BLACK};
    private static final int[] FADE_COLORS_REVERSE = new int[]{Color.BLACK, Color.TRANSPARENT};

    private boolean fadeTop, fadeBottom, fadeLeft, fadeRight;
    private int gradientSize, gradientSizeTop, gradientSizeBottom, gradientSizeLeft, gradientSizeRight;
    private Paint gradientPaintTop, gradientPaintBottom, gradientPaintLeft, gradientPaintRight;
    private Rect gradientRectTop, gradientRectBottom, gradientRectLeft, gradientRectRight;
    private int gradientDirtyFlags;

    public FadingEdgeRecyclerView(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    public FadingEdgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FadingEdgeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        int defaultSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_GRADIENT_SIZE_DP,
                getResources().getDisplayMetrics());

        if (attrs != null) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.FadingEdgeRecyclerView, defStyleAttr, 0);
            int flags = arr.getInt(R.styleable.FadingEdgeRecyclerView_fadingEdge, 0);

            fadeTop = (flags & FADE_EDGE_TOP) == FADE_EDGE_TOP;
            fadeBottom = (flags & FADE_EDGE_BOTTOM) == FADE_EDGE_BOTTOM;
            fadeLeft = (flags & FADE_EDGE_LEFT) == FADE_EDGE_LEFT;
            fadeRight = (flags & FADE_EDGE_RIGHT) == FADE_EDGE_RIGHT;

            gradientSize = arr.getDimensionPixelSize(R.styleable.FadingEdgeRecyclerView_fadingEdgeLength, 0);

            gradientSizeTop = arr.getDimensionPixelSize(R.styleable.FadingEdgeRecyclerView_fadingEdgeTopLength, defaultSize);
            gradientSizeBottom = arr.getDimensionPixelSize(R.styleable.FadingEdgeRecyclerView_fadingEdgeBottomLength, defaultSize);
            gradientSizeLeft = arr.getDimensionPixelSize(R.styleable.FadingEdgeRecyclerView_fadingEdgeLeftLength, defaultSize);
            gradientSizeRight = arr.getDimensionPixelSize(R.styleable.FadingEdgeRecyclerView_fadingEdgeRightLength, defaultSize);

            if (fadeTop && gradientSizeTop > 0) {
                gradientDirtyFlags |= DIRTY_FLAG_TOP;
            }
            if (fadeLeft && gradientSizeLeft > 0) {
                gradientDirtyFlags |= DIRTY_FLAG_LEFT;
            }
            if (fadeBottom && gradientSizeBottom > 0) {
                gradientDirtyFlags |= DIRTY_FLAG_BOTTOM;
            }
            if (fadeRight && gradientSizeRight > 0) {
                gradientDirtyFlags |= DIRTY_FLAG_RIGHT;
            }

            if (gradientSize > 0) {
                gradientSizeTop = gradientSize;
                gradientSizeBottom = gradientSize;
                gradientSizeLeft = gradientSize;
                gradientSizeRight = gradientSize;
            }

            arr.recycle();
        } else {
            gradientSizeTop = gradientSizeBottom = gradientSizeLeft = gradientSizeRight = defaultSize;
        }

        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        gradientPaintTop = new Paint(Paint.ANTI_ALIAS_FLAG);
        gradientPaintTop.setXfermode(mode);
        gradientPaintBottom = new Paint(Paint.ANTI_ALIAS_FLAG);
        gradientPaintBottom.setXfermode(mode);
        gradientPaintLeft = new Paint(Paint.ANTI_ALIAS_FLAG);
        gradientPaintLeft.setXfermode(mode);
        gradientPaintRight = new Paint(Paint.ANTI_ALIAS_FLAG);
        gradientPaintRight.setXfermode(mode);

        gradientRectTop = new Rect();
        gradientRectLeft = new Rect();
        gradientRectBottom = new Rect();
        gradientRectRight = new Rect();
    }

    public void setFadeSizes(int top, int left, int bottom, int right) {
        if (gradientSizeTop != top) {
            gradientSizeTop = top;
            gradientDirtyFlags |= DIRTY_FLAG_TOP;
        }
        if (gradientSizeLeft != left) {
            gradientSizeLeft = left;
            gradientDirtyFlags |= DIRTY_FLAG_LEFT;
        }
        if (gradientSizeBottom != bottom) {
            gradientSizeBottom = bottom;
            gradientDirtyFlags |= DIRTY_FLAG_BOTTOM;
        }
        if (gradientSizeRight != right) {
            gradientSizeRight = right;
            gradientDirtyFlags |= DIRTY_FLAG_RIGHT;
        }
        if (gradientDirtyFlags != 0) {
            invalidate();
        }
    }

    public void setFadeEdges(boolean fadeTop, boolean fadeLeft, boolean fadeBottom, boolean fadeRight) {
        if (this.fadeTop != fadeTop) {
            this.fadeTop = fadeTop;
            gradientDirtyFlags |= DIRTY_FLAG_TOP;
        }
        if (this.fadeLeft != fadeLeft) {
            this.fadeLeft = fadeLeft;
            gradientDirtyFlags |= DIRTY_FLAG_LEFT;
        }
        if (this.fadeBottom != fadeBottom) {
            this.fadeBottom = fadeBottom;
            gradientDirtyFlags |= DIRTY_FLAG_BOTTOM;
        }
        if (this.fadeRight != fadeRight) {
            this.fadeRight = fadeRight;
            gradientDirtyFlags |= DIRTY_FLAG_RIGHT;
        }
        if (gradientDirtyFlags != 0) {
            invalidate();
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (getPaddingLeft() != left) {
            gradientDirtyFlags |= DIRTY_FLAG_LEFT;
        }
        if (getPaddingTop() != top) {
            gradientDirtyFlags |= DIRTY_FLAG_TOP;
        }
        if (getPaddingRight() != right) {
            gradientDirtyFlags |= DIRTY_FLAG_RIGHT;
        }
        if (getPaddingBottom() != bottom) {
            gradientDirtyFlags |= DIRTY_FLAG_BOTTOM;
        }
        super.setPadding(left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw) {
            gradientDirtyFlags |= DIRTY_FLAG_LEFT;
            gradientDirtyFlags |= DIRTY_FLAG_RIGHT;
        }
        if (h != oldh) {
            gradientDirtyFlags |= DIRTY_FLAG_TOP;
            gradientDirtyFlags |= DIRTY_FLAG_BOTTOM;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int newWidth = getWidth(), newHeight = getHeight();
        boolean fadeAnyEdge = fadeTop || fadeBottom || fadeLeft || fadeRight;
        if (getVisibility() == GONE || newWidth == 0 || newHeight == 0 || !fadeAnyEdge) {
            super.dispatchDraw(canvas);
            return;
        }

        if ((gradientDirtyFlags & DIRTY_FLAG_TOP) == DIRTY_FLAG_TOP) {
            gradientDirtyFlags &= ~DIRTY_FLAG_TOP;
            initTopGradient();
        }
        if ((gradientDirtyFlags & DIRTY_FLAG_LEFT) == DIRTY_FLAG_LEFT) {
            gradientDirtyFlags &= ~DIRTY_FLAG_LEFT;
            initLeftGradient();
        }
        if ((gradientDirtyFlags & DIRTY_FLAG_BOTTOM) == DIRTY_FLAG_BOTTOM) {
            gradientDirtyFlags &= ~DIRTY_FLAG_BOTTOM;
            initBottomGradient();
        }
        if ((gradientDirtyFlags & DIRTY_FLAG_RIGHT) == DIRTY_FLAG_RIGHT) {
            gradientDirtyFlags &= ~DIRTY_FLAG_RIGHT;
            initRightGradient();
        }


        int count = canvas.saveLayer(new RectF(0.0f, 0.0f, (float) getWidth(), (float) getHeight()), null, ALL_SAVE_FLAG);

        super.dispatchDraw(canvas);
        if (fadeTop && gradientSizeTop > 0) {
            canvas.drawRect(gradientRectTop, gradientPaintTop);
        }
        if (fadeBottom && gradientSizeBottom > 0) {
            canvas.drawRect(gradientRectBottom, gradientPaintBottom);
        }
        if (fadeLeft && gradientSizeLeft > 0) {
            canvas.drawRect(gradientRectLeft, gradientPaintLeft);
        }
        if (fadeRight && gradientSizeRight > 0) {
            canvas.drawRect(gradientRectRight, gradientPaintRight);
        }
        canvas.restoreToCount(count);
    }

    private void initTopGradient() {
        int actualHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int size = Math.min(gradientSizeTop, actualHeight);
        int l = getPaddingLeft();
        int t = getPaddingTop();
        int r = getWidth() - getPaddingRight();
        int b = t + size;
        gradientRectTop.set(l, t, r, b);
        LinearGradient gradient = new LinearGradient(l, t, l, b, FADE_COLORS, null, Shader.TileMode.CLAMP);
        gradientPaintTop.setShader(gradient);
    }

    private void initLeftGradient() {
        int actualWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int size = Math.min(gradientSizeLeft, actualWidth);
        int l = getPaddingLeft();
        int t = getPaddingTop();
        int r = l + size;
        int b = getHeight() - getPaddingBottom();
        gradientRectLeft.set(l, t, r, b);
        LinearGradient gradient = new LinearGradient(l, t, r, t, FADE_COLORS, null, Shader.TileMode.CLAMP);
        gradientPaintLeft.setShader(gradient);
    }

    private void initBottomGradient() {
        int actualHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int size = Math.min(gradientSizeBottom, actualHeight);
        int l = getPaddingLeft();
        int t = getPaddingTop() + actualHeight - size;
        int r = getWidth() - getPaddingRight();
        int b = t + size;
        gradientRectBottom.set(l, t, r, b);
        LinearGradient gradient = new LinearGradient(l, t, l, b, FADE_COLORS_REVERSE, null, Shader.TileMode.CLAMP);
        gradientPaintBottom.setShader(gradient);
    }

    private void initRightGradient() {
        int actualWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int size = Math.min(gradientSizeRight, actualWidth);
        int l = getPaddingLeft() + actualWidth - size;
        int t = getPaddingTop();
        int r = l + size;
        int b = getHeight() - getPaddingBottom();
        gradientRectRight.set(l, t, r, b);
        LinearGradient gradient = new LinearGradient(l, t, r, t, FADE_COLORS_REVERSE, null, Shader.TileMode.CLAMP);
        gradientPaintRight.setShader(gradient);
    }
}
