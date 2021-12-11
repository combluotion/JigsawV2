package com.uocp8.jigsawv2.viewmodel;

import static com.uocp8.jigsawv2.util.Constants.INVALID_ID;
import static com.uocp8.jigsawv2.util.Constants.MOVE_DURATION;
import static com.uocp8.jigsawv2.util.Constants.SMOOTH_SCROLL_AMOUNT_AT_EDGE;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.uocp8.jigsawv2.MainActivity;
import com.uocp8.jigsawv2.R;
import com.uocp8.jigsawv2.adapters.OrderableAdapter;
import com.uocp8.jigsawv2.dao.ScoreDao;
import com.uocp8.jigsawv2.dao.impl.ScoreDaoImpl;
import com.uocp8.jigsawv2.model.Difficulty;
import com.uocp8.jigsawv2.model.ImageEntity;
import com.uocp8.jigsawv2.model.MyCalendar;
import com.uocp8.jigsawv2.model.Score;
import com.uocp8.jigsawv2.util.GridUtil;
import com.uocp8.jigsawv2.util.PermissionsUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class GameViewModel extends GridView {

    public boolean IsChangingActivity;

    Handler handlerUI = new Handler();

    private BitmapDrawable mHoverCell;
    private Rect mHoverCellCurrentBounds;
    private Rect mHoverCellOriginalBounds;

    private int mTotalOffsetY = 0;
    private int mTotalOffsetX = 0;

    private int mDownX = -1;
    private int mDownY = -1;
    private int mLastEventY = -1;
    private int mLastEventX = -1;

    // used to distinguish straight line and diagonal switching
    private int mOverlapIfSwitchStraightLine;

    private List<Long> idList = new ArrayList<>();

    private long mMobileItemId = INVALID_ID;

    private boolean mCellIsMobile = false;
    private int mActivePointerId = INVALID_ID;

    private boolean mIsMobileScrolling;
    private int mSmoothScrollAmountAtEdge = 0;
    private boolean mIsWaitingForScrollFinish = false;
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    private boolean mIsEditMode = false;
    private boolean mHoverAnimation;
    private boolean mReorderAnimation;

    private OnScrollListener mUserScrollListener;
    private OnDropListener mDropListener;
    private OnDragListener mDragListener;

    private Difficulty level;
    private MediaPlayer movePiece;
    private MediaPlayer dropPiece;
    private OnItemClickListener mUserItemClickListener;
    private OnItemClickListener mLocalItemClickListener = new
            OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    if (!isEditMode() && isEnabled() && mUserItemClickListener !=
                            null) {
                        mUserItemClickListener.onItemClick(parent, view, position, id);
                    }
                }
            };

    private View mMobileView;
    private OnScrollListener mScrollListener = new OnScrollListener() {

        private int mPreviousFirstVisibleItem = -1;
        private int mPreviousVisibleItemCount = -1;
        private int mCurrentFirstVisibleItem;
        private int mCurrentVisibleItemCount;
        private int mCurrentScrollState;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mCurrentScrollState = scrollState;
            mScrollState = scrollState;
            isScrollCompleted();
            if (mUserScrollListener != null) {
                mUserScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            mCurrentFirstVisibleItem = firstVisibleItem;
            mCurrentVisibleItemCount = visibleItemCount;

            mPreviousFirstVisibleItem = (mPreviousFirstVisibleItem == -1) ?
                    mCurrentFirstVisibleItem
                    : mPreviousFirstVisibleItem;
            mPreviousVisibleItemCount = (mPreviousVisibleItemCount == -1) ?
                    mCurrentVisibleItemCount
                    : mPreviousVisibleItemCount;

            checkAndHandleFirstVisibleCellChange();
            checkAndHandleLastVisibleCellChange();

            mPreviousFirstVisibleItem = mCurrentFirstVisibleItem;
            mPreviousVisibleItemCount = mCurrentVisibleItemCount;

            if (mUserScrollListener != null) {
                mUserScrollListener.onScroll(view, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }
        }

        void checkAndHandleFirstVisibleCellChange() {
            if (mCurrentFirstVisibleItem != mPreviousFirstVisibleItem && (mCellIsMobile && mMobileItemId != INVALID_ID)) {
                updateNeighborViewsForId(mMobileItemId);
                handleCellSwitch();
            }
        }

        void checkAndHandleLastVisibleCellChange() {
            int currentLastVisibleItem = mCurrentFirstVisibleItem
                    + mCurrentVisibleItemCount;
            int previousLastVisibleItem = mPreviousFirstVisibleItem
                    + mPreviousVisibleItemCount;
            if (currentLastVisibleItem != previousLastVisibleItem && (mCellIsMobile && mMobileItemId != INVALID_ID)) {
                updateNeighborViewsForId(mMobileItemId);
                handleCellSwitch();
            }
        }

        private void isScrollCompleted() {
            if (mCurrentVisibleItemCount > 0
                    && mCurrentScrollState == SCROLL_STATE_IDLE) {
                if (mCellIsMobile && mIsMobileScrolling) {
                    handleMobileCellScroll();
                } else if (mIsWaitingForScrollFinish) {
                    touchEventsEnded();
                }
            }
        }
    };

    public GameViewModel(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        super.setOnScrollListener(mScrollListener);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mSmoothScrollAmountAtEdge = (int) (SMOOTH_SCROLL_AMOUNT_AT_EDGE
                * metrics.density + 0.5f);
        mOverlapIfSwitchStraightLine = getResources().getDimensionPixelSize(
                R.dimen.dgv_overlap_if_switch_straight_line);

        movePiece = MediaPlayer.create(getContext(),R.raw.getpiece);
        dropPiece = MediaPlayer.create(getContext(),R.raw.droppiece);

    }

    public GameViewModel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameViewModel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public static boolean isPreLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public void setOnScrollListener(OnScrollListener scrollListener) {
        this.mUserScrollListener = scrollListener;
    }

    public void setDifficultyLevel(Difficulty level)
    {
        this.level = level;
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHoverCell != null) {
            mHoverCell.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                mActivePointerId = event.getPointerId(0);
                if (mIsEditMode && isEnabled()) {
                    layoutChildren();
                    int position = pointToPosition(mDownX, mDownY);
                    startDragAtPosition(position);
                } else if (!isEnabled()) {
                    return false;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_ID) {
                    break;
                }

                int pointerIndex = event.findPointerIndex(mActivePointerId);

                mLastEventY = (int) event.getY(pointerIndex);
                mLastEventX = (int) event.getX(pointerIndex);
                int deltaY = mLastEventY - mDownY;
                int deltaX = mLastEventX - mDownX;

                if (mCellIsMobile) {
                    mHoverCellCurrentBounds.offsetTo(
                            mHoverCellOriginalBounds.left
                                    + deltaX + mTotalOffsetX,
                            mHoverCellOriginalBounds.top
                                    + deltaY + mTotalOffsetY);
                    mHoverCell.setBounds(mHoverCellCurrentBounds);
                    invalidate();
                    handleCellSwitch();
                    mIsMobileScrolling = false;
                    handleMobileCellScroll();
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                touchEventsEnded();

                if (mHoverCell != null && mDropListener != null) {
                    mDropListener.onActionDrop();
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                touchEventsCancelled();

                if (mHoverCell != null && mDropListener != null) {
                    mDropListener.onActionDrop();
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = (event.getAction() & MotionEvent
                        .ACTION_POINTER_INDEX_MASK) >> MotionEvent
                        .ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    touchEventsEnded();
                }
                break;

            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    public void setOnDropListener(OnDropListener dropListener) {
        this.mDropListener = dropListener;
    }

    public void setOnDragListener(OnDragListener dragListener) {
        this.mDragListener = dragListener;
    }

    public void startEditMode(int position) {
        movePiece.start();
        requestDisallowInterceptTouchEvent(true);
        if (isPostHoneycomb() && position != -1) {
            startDragAtPosition(position);
        }
        mIsEditMode = true;
    }

    private boolean isPostHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    private void startDragAtPosition(int position) {
        mTotalOffsetY = 0;
        mTotalOffsetX = 0;
        int itemNum = position - getFirstVisiblePosition();
        View selectedView = getChildAt(itemNum);
        if (selectedView != null) {
            mMobileItemId = getAdapter().getItemId(position);
            mHoverCell = getAndAddHoverView(selectedView);
            if (isPostHoneycomb())
                selectedView.setVisibility(View.INVISIBLE);
            mCellIsMobile = true;
            updateNeighborViewsForId(mMobileItemId);
            if (mDragListener != null) {
                mDragListener.onDragStarted(position);
            }
        }
    }

    private BitmapDrawable getAndAddHoverView(View v) {

        int w = v.getWidth();
        int h = v.getHeight();
        int top = v.getTop();
        int left = v.getLeft();

        Bitmap b = getBitmapFromView(v);

        BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

        mHoverCellOriginalBounds = new Rect(left, top, left + w, top + h);
        mHoverCellCurrentBounds = new Rect(mHoverCellOriginalBounds);

        drawable.setBounds(mHoverCellCurrentBounds);

        return drawable;
    }

    private void updateNeighborViewsForId(long itemId) {
        idList.clear();
        int draggedPos = getPositionForID(itemId);
        for (int pos = getFirstVisiblePosition(); pos <=
                getLastVisiblePosition(); pos++) {
            if (draggedPos != pos && getAdapterInterface().canReorder(pos)) {
                idList.add(getId(pos));
            }
        }
    }

    private Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    public int getPositionForID(long itemId) {
        View v = getViewForId(itemId);
        if (v == null) {
            return -1;
        } else {
            return getPositionForView(v);
        }
    }

    private OrderableAdapter getAdapterInterface() {
        return (OrderableAdapter) getAdapter();
    }

    private long getId(int position) {
        return getAdapter().getItemId(position);
    }

    public View getViewForId(long itemId) {
        int firstVisiblePosition = getFirstVisiblePosition();
        ListAdapter adapter = getAdapter();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int position = firstVisiblePosition + i;
            long id = adapter.getItemId(position);
            if (id == itemId) {
                return v;
            }
        }
        return null;
    }

    public void stopEditMode(Chronometer chronometer) {
        dropPiece.start();
        mIsEditMode = false;
        requestDisallowInterceptTouchEvent(false);
        checkCurrentPositions(chronometer);
    }

    /**
     * * Comprobación para cierre de llamadas
     *   Se ejecuta cada vez que suelta una pieza para ver si sus posiciones son las correctas,
     *   En caso de que todas tengan la posición correcta, indica el parámetro isDone y llama a la función de finalizado
     */
    public void checkCurrentPositions(Chronometer chronometer)
    {
        GridView grid = findViewById(R.id.jigsaw_grid);
        int first = grid.getFirstVisiblePosition();
        int last = grid.getLastVisiblePosition();
        boolean isDone = true;
        for(int i=first; i<=last; ++i){

            //ImageView item = (ImageView) grid.getItemAtPosition(i); // You should probably cast to your adapter's item type
            ImageEntity item = (ImageEntity) grid.getItemAtPosition(i);
            //int idealPosition = (int) item.getTag(R.id.IDEAL_POSITION);
            int idealPosition = Math.toIntExact(item.getIdealPosition());
            Log.d("message","IdealPosition " + idealPosition + " Posicion: " + i);
            if(i != idealPosition)
            {
                isDone = false;
            }
        }
        if(isDone)
        {
            String chronoTime = chronometer.getText().toString();
            long seconds = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;

            chronometer.stop();
            Toast.makeText(getContext(),"¡Felicidades! Has ganado.", Toast.LENGTH_LONG).show();

            handlerUI.postDelayed(new Runnable() {
                @Override
                public void run() {
                    endGame(chronoTime,seconds );
                }
            }, 500);



        }
    }
    public void endGame(String chronoTime, long seconds)
    {
        IsChangingActivity = true;
        //Score Time:
        long finalScore = (long) (level.getScoreInitial() - seconds) * (level.getValue() / 2);

        //Ask for Name
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(chronoTime +" ! Inserta tu nombre:");

        //Indicamos el input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        //Añadimos botones
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), input.getText().toString(), Toast.LENGTH_LONG).show();
                //Create Score
                String nombre = input.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); //yyyyMMdd_HHmmss
                String currentDateandTime = sdf.format(new Date());
                ScoreDao scoreDao = new ScoreDaoImpl(getContext());
                Score score = new Score(nombre,currentDateandTime,finalScore);
                if (nombre.isEmpty()){

                    score.setName("No name");

                }
                    scoreDao.create(score);

                //insertar un evento en le calendario
                Intent intent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(intent);

               Intent calendar = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.Events.TITLE,"SCORE")
                        .putExtra(CalendarContract.Events.DESCRIPTION, Long.toString(finalScore));

                        getContext().startActivity(calendar);

              /*  MyCalendar[] calendarios = PermissionsUtil.getCalendar(getContext());

                PermissionsUtil.checkPermission(42,getContext(), Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);

                ContentResolver cr = getContext().getContentResolver();
                ContentValues cv = new ContentValues();
                cv.put(CalendarContract.Events.TITLE, "Partida Jigsaw");
                cv.put(CalendarContract.Events.DESCRIPTION, "Has ganado " + finalScore + " puntos.");
                cv.put(CalendarContract.Events.EVENT_LOCATION, "Jigsaw");
                cv.put(CalendarContract.Events.DTSTART, Calendar.getInstance().getTimeInMillis());
                cv.put(CalendarContract.Events.DTEND, Calendar.getInstance().getTimeInMillis() + 60000);
                cv.put(CalendarContract.Events.CALENDAR_ID, calendarios[2].getCalId());
                cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);
                Toast.makeText(getContext(), "Añadido evento en calendario", Toast.LENGTH_LONG).show();*/

            }
        });
        builder.setNegativeButton("No quiero", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //Return to Home menu
                Intent intent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(intent);
            }
        });

        builder.show();

    }

    /**
     * END Finalización
     */
    public boolean isEditMode() {
        return mIsEditMode;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mUserItemClickListener = listener;
        super.setOnItemClickListener(mLocalItemClickListener);
    }

    private void reorderElements(int originalPosition, int targetPosition) {
        if (mDragListener != null)
            mDragListener.onDragPositionsChanged(originalPosition,
                    targetPosition);
        getAdapterInterface().reorderItems(originalPosition, targetPosition);
    }

    private void handleMobileCellScroll() {
        mIsMobileScrolling = handleMobileCellScroll(mHoverCellCurrentBounds);
    }

    public boolean handleMobileCellScroll(Rect r) {
        int offset = computeVerticalScrollOffset();
        int height = getHeight();
        int extent = computeVerticalScrollExtent();
        int range = computeVerticalScrollRange();
        int hoverViewTop = r.top;
        int hoverHeight = r.height();

        if (hoverViewTop <= 0 && offset > 0) {
            smoothScrollBy(-mSmoothScrollAmountAtEdge, 0);
            return true;
        }

        if (hoverViewTop + hoverHeight >= height && (offset + extent) < range) {
            smoothScrollBy(mSmoothScrollAmountAtEdge, 0);
            return true;
        }

        return false;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    private void touchEventsEnded() {
        final View mobileView = getViewForId(mMobileItemId);
        if (mobileView != null && (mCellIsMobile || mIsWaitingForScrollFinish)) {
            mCellIsMobile = false;
            mIsWaitingForScrollFinish = false;
            mIsMobileScrolling = false;
            mActivePointerId = INVALID_ID;
            if (mScrollState != OnScrollListener.SCROLL_STATE_IDLE) {
                mIsWaitingForScrollFinish = true;
                return;
            }

            mHoverCellCurrentBounds.offsetTo(mobileView.getLeft(),
                    mobileView.getTop());

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                animateBounds(mobileView);
            } else {
                mHoverCell.setBounds(mHoverCellCurrentBounds);
                invalidate();
                reset(mobileView);
            }
        } else {
            touchEventsCancelled();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animateBounds(final View mobileView) {
        TypeEvaluator<Rect> sBoundEvaluator = new TypeEvaluator<Rect>() {
            public Rect evaluate(float fraction, Rect startValue, Rect
                    endValue) {
                return new Rect(interpolate(startValue.left, endValue.left,
                        fraction), interpolate(startValue.top, endValue.top,
                        fraction), interpolate(startValue.right,
                        endValue.right, fraction), interpolate(
                        startValue.bottom, endValue.bottom, fraction));
            }

            int interpolate(int start, int end, float fraction) {
                return (int) (start + fraction * (end - start));
            }
        };

        ObjectAnimator hoverViewAnimator = ObjectAnimator.ofObject(mHoverCell,
                "bounds", sBoundEvaluator, mHoverCellCurrentBounds);
        hoverViewAnimator.addUpdateListener(valueAnimator -> invalidate());

        hoverViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHoverAnimation = false;
                updateEnableState();
                reset(mobileView);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mHoverAnimation = true;
                updateEnableState();
            }


        });
        hoverViewAnimator.start();
    }

    private void reset(View mobileView) {
        idList.clear();
        mMobileItemId = INVALID_ID;
        mobileView.setVisibility(View.VISIBLE);
        mHoverCell = null;

        // ugly fix for unclear disappearing items after reorder
        for (int i = 0; i < getLastVisiblePosition()
                - getFirstVisiblePosition(); i++) {
            View child = getChildAt(i);
            if (child != null) {
                child.setVisibility(View.VISIBLE);
            }
        }
        invalidate();
    }

    private void touchEventsCancelled() {
        View mobileView = getViewForId(mMobileItemId);
        if (mCellIsMobile) {
            reset(mobileView);
        }
        mCellIsMobile = false;
        mIsMobileScrolling = false;
        mActivePointerId = INVALID_ID;

    }

    private void handleCellSwitch() {
        final int deltaY = mLastEventY - mDownY;
        final int deltaX = mLastEventX - mDownX;
        final int deltaYTotal = mHoverCellOriginalBounds.centerY()
                + mTotalOffsetY + deltaY;
        final int deltaXTotal = mHoverCellOriginalBounds.centerX()
                + mTotalOffsetX + deltaX;
        mMobileView = getViewForId(mMobileItemId);
        View targetView = null;
        float vX = 0;
        float vY = 0;
        Point mobileColumnRowPair = getColumnAndRowForView(mMobileView);
        for (Long id : idList) {
            View view = getViewForId(id);
            if (view != null) {
                Point targetColumnRowPair = getColumnAndRowForView(view);
                if (aboveRight(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal < view.getBottom()
                        && deltaXTotal > view.getLeft()
                        || aboveLeft(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal < view.getBottom()
                        && deltaXTotal < view.getRight()
                        || belowRight(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal > view.getTop()
                        && deltaXTotal > view.getLeft()
                        || belowLeft(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal > view.getTop()
                        && deltaXTotal < view.getRight()
                        || above(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal < view.getBottom()
                        - mOverlapIfSwitchStraightLine
                        || below(targetColumnRowPair, mobileColumnRowPair)
                        && deltaYTotal > view.getTop()
                        + mOverlapIfSwitchStraightLine
                        || right(targetColumnRowPair, mobileColumnRowPair)
                        && deltaXTotal > view.getLeft()
                        + mOverlapIfSwitchStraightLine || left(
                        targetColumnRowPair, mobileColumnRowPair)
                        && deltaXTotal < view.getRight()
                        - mOverlapIfSwitchStraightLine) {
                    float xDiff = Math.abs(GridUtil.getViewX(view)
                            - GridUtil.getViewX(mMobileView));
                    float yDiff = Math.abs(GridUtil.getViewY(view)
                            - GridUtil.getViewY(mMobileView));
                    if (xDiff >= vX && yDiff >= vY) {
                        vX = xDiff;
                        vY = yDiff;
                        targetView = view;
                    }
                }
            }
        }
        if (targetView != null) {
            final int originalPosition = getPositionForView(mMobileView);
            int targetPosition = getPositionForView(targetView);

            final OrderableAdapter adapter = getAdapterInterface();
            if (targetPosition == INVALID_POSITION
                    || !adapter.canReorder(originalPosition)
                    || !adapter.canReorder(targetPosition)) {
                updateNeighborViewsForId(mMobileItemId);
                return;
            }
            reorderElements(originalPosition, targetPosition);

            mDownY = mLastEventY;
            mDownX = mLastEventX;

            SwitchCellAnimator switchCellAnimator;

            if (isPostHoneycomb() && isPreLollipop()) // Between Android 3.0 and
                // Android L
                switchCellAnimator = new KitKatSwitchCellAnimator(deltaX,
                        deltaY);
            else if (isPreLollipop()) // Before Android 3.0
                switchCellAnimator = new PreHoneycombCellAnimator(deltaX,
                        deltaY);
            else
                // Android L
                switchCellAnimator = new LSwitchCellAnimator(deltaX, deltaY);

            updateNeighborViewsForId(mMobileItemId);

            switchCellAnimator.animateSwitchCell(originalPosition,
                    targetPosition);
        }
    }

    private boolean belowLeft(Point targetColumnRowPair,
                              Point mobileColumnRowPair) {
        return targetColumnRowPair.y > mobileColumnRowPair.y
                && targetColumnRowPair.x < mobileColumnRowPair.x;
    }

    private boolean belowRight(Point targetColumnRowPair,
                               Point mobileColumnRowPair) {
        return targetColumnRowPair.y > mobileColumnRowPair.y
                && targetColumnRowPair.x > mobileColumnRowPair.x;
    }

    private boolean aboveLeft(Point targetColumnRowPair,
                              Point mobileColumnRowPair) {
        return targetColumnRowPair.y < mobileColumnRowPair.y
                && targetColumnRowPair.x < mobileColumnRowPair.x;
    }

    private boolean aboveRight(Point targetColumnRowPair,
                               Point mobileColumnRowPair) {
        return targetColumnRowPair.y < mobileColumnRowPair.y
                && targetColumnRowPair.x > mobileColumnRowPair.x;
    }

    private boolean above(Point targetColumnRowPair, Point
            mobileColumnRowPair) {
        return targetColumnRowPair.y < mobileColumnRowPair.y
                && targetColumnRowPair.x == mobileColumnRowPair.x;
    }

    private boolean below(Point targetColumnRowPair, Point
            mobileColumnRowPair) {
        return targetColumnRowPair.y > mobileColumnRowPair.y
                && targetColumnRowPair.x == mobileColumnRowPair.x;
    }

    private boolean right(Point targetColumnRowPair, Point
            mobileColumnRowPair) {
        return targetColumnRowPair.y == mobileColumnRowPair.y
                && targetColumnRowPair.x > mobileColumnRowPair.x;
    }

    private boolean left(Point targetColumnRowPair, Point mobileColumnRowPair) {
        return targetColumnRowPair.y == mobileColumnRowPair.y
                && targetColumnRowPair.x < mobileColumnRowPair.x;
    }

    private Point getColumnAndRowForView(View view) {
        int pos = getPositionForView(view);
        int columns = getColumnCount();
        int column = pos % columns;
        int row = pos / columns;
        return new Point(column, row);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animateReorder(final int oldPosition, final int newPosition) {
        boolean isForward = newPosition > oldPosition;
        List<Animator> resultList = new LinkedList<>();
        if (isForward) {
            for (int pos = Math.min(oldPosition, newPosition); pos < Math.max(
                    oldPosition, newPosition); pos++) {
                View view = getViewForId(getId(pos));
                if ((pos + 1) % getColumnCount() == 0) {
                    resultList.add(createTranslationAnimations(view,
                            -view.getWidth() * (getColumnCount() - 1), 0,
                            view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view,
                            view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            for (int pos = Math.max(oldPosition, newPosition); pos > Math.min(
                    oldPosition, newPosition); pos--) {
                View view = getViewForId(getId(pos));
                if ((pos + getColumnCount()) % getColumnCount() == 0) {
                    resultList.add(createTranslationAnimations(view,
                            view.getWidth() * (getColumnCount() - 1), 0,
                            -view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view,
                            -view.getWidth(), 0, 0, 0));
                }
            }
        }

        AnimatorSet resultSet = new AnimatorSet();
        resultSet.playTogether(resultList);
        resultSet.setDuration(MOVE_DURATION);
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mReorderAnimation = true;
                updateEnableState();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mReorderAnimation = false;
                updateEnableState();
            }
        });
        resultSet.start();
    }

    private int getColumnCount() {
        return getAdapterInterface().getColumnCount();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private AnimatorSet createTranslationAnimations(View view, float startX,
                                                    float endX, float startY,
                                                    float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
                startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
                startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    private void updateEnableState() {
        setEnabled(!mHoverAnimation && !mReorderAnimation);
    }

    private interface SwitchCellAnimator {
        void animateSwitchCell(final int originalPosition,
                               final int targetPosition);
    }

    public interface OnDropListener {
        void onActionDrop();
    }

    public interface OnDragListener {
        void onDragStarted(int position);

        void onDragPositionsChanged(int oldPosition, int newPosition);
    }

    private class PreHoneycombCellAnimator implements SwitchCellAnimator {
        private int mDeltaY;
        private int mDeltaX;

        PreHoneycombCellAnimator(int deltaX, int deltaY) {
            mDeltaX = deltaX;
            mDeltaY = deltaY;
        }

        @Override
        public void animateSwitchCell(int originalPosition, int
                targetPosition) {
            mTotalOffsetY += mDeltaY;
            mTotalOffsetX += mDeltaX;
        }
    }

    private class KitKatSwitchCellAnimator implements SwitchCellAnimator {

        private int mDeltaY;
        private int mDeltaX;

        KitKatSwitchCellAnimator(int deltaX, int deltaY) {
            mDeltaX = deltaX;
            mDeltaY = deltaY;
        }

        private class AnimateSwitchViewOnPreDrawListener implements
                ViewTreeObserver.OnPreDrawListener {

            private final View mPreviousMobileView;
            private final int mOriginalPosition;
            private final int mTargetPosition;

            AnimateSwitchViewOnPreDrawListener(final View previousMobileView,
                                               final int originalPosition,
                                               final int targetPosition) {
                mPreviousMobileView = previousMobileView;
                mOriginalPosition = originalPosition;
                mTargetPosition = targetPosition;
            }

            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);

                mTotalOffsetY += mDeltaY;
                mTotalOffsetX += mDeltaX;

                animateReorder(mOriginalPosition, mTargetPosition);

                mPreviousMobileView.setVisibility(View.VISIBLE);

                if (mMobileView != null) {
                    mMobileView.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        }

        @Override
        public void animateSwitchCell(final int originalPosition,
                                      final int targetPosition) {
            assert mMobileView != null;
            getViewTreeObserver().addOnPreDrawListener(
                    new AnimateSwitchViewOnPreDrawListener(mMobileView,
                            originalPosition, targetPosition));
            mMobileView = getViewForId(mMobileItemId);
        }


    }

    private class LSwitchCellAnimator implements SwitchCellAnimator {

        private int mDeltaY;
        private int mDeltaX;

        LSwitchCellAnimator(int deltaX, int deltaY) {
            mDeltaX = deltaX;
            mDeltaY = deltaY;
        }

        private class AnimateSwitchViewOnPreDrawListener implements
                ViewTreeObserver.OnPreDrawListener {
            private final int mOriginalPosition;
            private final int mTargetPosition;

            AnimateSwitchViewOnPreDrawListener(final int originalPosition,
                                               final int targetPosition) {
                mOriginalPosition = originalPosition;
                mTargetPosition = targetPosition;
            }

            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);

                mTotalOffsetY += mDeltaY;
                mTotalOffsetX += mDeltaX;

                animateReorder(mOriginalPosition, mTargetPosition);

                assert mMobileView != null;
                mMobileView.setVisibility(View.VISIBLE);
                mMobileView = getViewForId(mMobileItemId);
                assert mMobileView != null;
                mMobileView.setVisibility(View.INVISIBLE);
                return true;
            }
        }

        @Override
        public void animateSwitchCell(final int originalPosition, final int targetPosition) {
            getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener(originalPosition, targetPosition));
        }
    }
}
