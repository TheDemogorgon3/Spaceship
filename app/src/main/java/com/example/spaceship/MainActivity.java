package com.example.spaceship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ImageView img, img2, img3, tunnelTop, tunnelBottom, helpIcon, settingsIcon, closeInfo, closeSettings, arrow, logo, pause, play;
    ImageView[] ends, coins;
    ImageButton switchPos;
    TextView scoreTxt, highscoreTxt, avgText, totalTxt, gamesTxt, title, info;
    Button start, stats;
    RelativeLayout settings, grid;
    ScrollView ships;
    Timer timer, coinTimer, collectTimer;
    Handler handler, coinHandler, collectHandler;
    int score, musicPos, coinCollect, endExplosion, negativeClick, positiveClick, switchClick;
    int[] combs;
    float rotation, soundVol;
    boolean canPlay;
    Random random;
    Object[][] sets;
    MediaPlayer mediaPlayer;
    SoundPool soundPool;
    SeekBar soundSeek, musicSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();

        tunnelTop = findViewById(R.id.tunnelTop);
        tunnelBottom = findViewById(R.id.tunnelBottom);
        helpIcon = findViewById(R.id.helpIcon);
        settingsIcon = findViewById(R.id.settingsIcon);
        info = findViewById(R.id.info);
        closeInfo = findViewById(R.id.closeInfo);
        settings = findViewById(R.id.settings);
        closeSettings = findViewById(R.id.closeSettings);
        img = findViewById(R.id.img);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        arrow = findViewById(R.id.arrow);
        switchPos = findViewById(R.id.switchPos);
        scoreTxt = findViewById(R.id.score);
        highscoreTxt = findViewById(R.id.highscore);
        avgText = findViewById(R.id.avg);
        totalTxt = findViewById(R.id.total);
        gamesTxt = findViewById(R.id.games);
        start = findViewById(R.id.start);
        logo = findViewById(R.id.logo);
        title = findViewById(R.id.title);
        pause = findViewById(R.id.pause);
        play = findViewById(R.id.play);
        grid = findViewById(R.id.grid);
        ships = findViewById(R.id.ships);
        stats = findViewById(R.id.stats);
        soundSeek = findViewById(R.id.soundSeek);
        musicSeek = findViewById(R.id.musicSeek);

        canPlay = true;
        info.setMovementMethod(new ScrollingMovementMethod());
        musicPos = 0;

        pause.setOnClickListener(view -> {
            playSoundEffect(negativeClick);
            canPlay = false;
            pause.setVisibility(View.INVISIBLE);
            play.setVisibility(View.VISIBLE);
        });

        play.setOnClickListener(view -> {
            playSoundEffect(positiveClick);
            play.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
            canPlay = true;
        });

        sets = new Object[][]{
                {R.drawable.sprite0, findViewById(R.id.st0t0), true, findViewById(R.id.st0s0)},
                {R.drawable.sprite1, findViewById(R.id.st1t1), true, findViewById(R.id.st1s1)},
                {R.drawable.sprite2, findViewById(R.id.st1t2), true, findViewById(R.id.st1s2)},
                {R.drawable.sprite3, findViewById(R.id.st1t3), true, findViewById(R.id.st1s3)},
                {R.drawable.sprite4, findViewById(R.id.st1t4), true, findViewById(R.id.st1s4)},
                {R.drawable.sprite5, findViewById(R.id.st2t1), true, findViewById(R.id.st2s1)},
                {R.drawable.sprite6, findViewById(R.id.st2t2), true, findViewById(R.id.st2s2)},
                {R.drawable.sprite7, findViewById(R.id.st2t3), true, findViewById(R.id.st2s3)},
                {R.drawable.sprite8, findViewById(R.id.st2t4), true, findViewById(R.id.st2s4)}
        };

        setSprites();

        random = new Random();

        ends = new ImageView[] {findViewById(R.id.end1), findViewById(R.id.end2), findViewById(R.id.end3), findViewById(R.id.end4)};

        coins = new ImageView[]{findViewById(R.id.coin1), findViewById(R.id.coin2), findViewById(R.id.coin3), findViewById(R.id.coin4), findViewById(R.id.coin5), findViewById(R.id.coin6), findViewById(R.id.coin7), findViewById(R.id.coin8)};
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    public void setSprites() {
        final String GREEN = "#36B32D", RED = "#FF0000";

        SharedPreferences sharedPreferences = getSharedPreferences("highscore", MODE_PRIVATE);
        String high = sharedPreferences.getString("high", "0");
        highscoreTxt.setText(high);
        String avg = sharedPreferences.getString("avg", "0");
        avgText.setText(avg);
        totalTxt.setText(sharedPreferences.getString("total", "0"));
        gamesTxt.setText(sharedPreferences.getString("games", "0"));
        int spriteID = sharedPreferences.getInt("sprite", R.drawable.sprite0);
        for (int i = 0; i < sets.length; i++) {
            TextView tv = (TextView) sets[i][1];
            if ((i > 0 && i < 5 && Integer.parseInt(tv.getText().toString()) > Double.parseDouble(avg)) || (i > 4 && Integer.parseInt(tv.getText().toString()) > Double.parseDouble(high))) {
                sets[i][2] = false;
                tv.setTextColor(Color.parseColor(RED));
            } else {
                sets[i][2] = true;
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            }
            if ((int) sets[i][0] == spriteID && (boolean) sets[i][2]) {
                tv.setTextColor(Color.parseColor(GREEN));
                logo.setImageResource(spriteID);
                img.setImageResource(spriteID);
            } else if ((int) sets[i][0] == spriteID) {
                ((TextView) findViewById(R.id.st0t0)).setTextColor(Color.parseColor(GREEN));
                logo.setImageResource(R.drawable.sprite0);
                img.setImageResource(R.drawable.sprite0);
                SharedPreferences preferences = getSharedPreferences("highscore", MODE_PRIVATE);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putInt("sprite", (int) sets[0][0]);
                edit.apply();
            }
            int finalI = i;
            ((ImageView)sets[i][3]).setOnClickListener(view -> {
                if ((boolean) sets[finalI][2]) {
                    playSoundEffect(positiveClick);
                    logo.setImageResource((int) sets[finalI][0]);
                    img.setImageResource((int) sets[finalI][0]);
                    for (Object[] set : sets) {
                        if (((TextView) set[1]).getCurrentTextColor() == Color.parseColor(GREEN)) {
                            ((TextView) set[1]).setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                    ((TextView) sets[finalI][1]).setTextColor(Color.parseColor(GREEN));
                    SharedPreferences preferences2 = getSharedPreferences("highscore", MODE_PRIVATE);
                    SharedPreferences.Editor edit2 = preferences2.edit();
                    edit2.putInt("sprite", (int) sets[finalI][0]);
                    edit2.apply();
                } else {
                    playSoundEffect(negativeClick);
                }
            });
        }
    }

    public void onStartClick(View view) {
        playSoundEffect(switchClick);

        scoreTxt.setText("0");
        title.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        start.setVisibility(View.INVISIBLE);
        stats.setVisibility(View.INVISIBLE);
        helpIcon.setVisibility(View.GONE);
        settingsIcon.setVisibility(View.GONE);
        grid.setVisibility(View.GONE);
        ships.setVisibility(View.GONE);

        img.setVisibility(View.VISIBLE);
        img2.setVisibility(View.VISIBLE);
        img3.setVisibility(View.VISIBLE);
        arrow.setVisibility(View.VISIBLE);
        switchPos.setVisibility(View.VISIBLE);
        pause.setVisibility(View.VISIBLE);

        arrow.setY(tunnelBottom.getY() + tunnelBottom.getHeight() + 20);
        arrow.setRotation(0);

        collectTimer = new Timer();
        collectHandler = new Handler();

        for (final ImageView coin: coins) {
            collectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    collectHandler.post(() -> {
                        if (canPlay) {
                            if (coin.getVisibility() == View.VISIBLE) {
                                coin.setVisibility(View.INVISIBLE);
                            } else {
                                coin.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }, 50, (random.nextInt(21) + 5) * 1000);
        }

        combs = new int[8];

        for (int i = 0; i < combs.length; i++) {
            combs[i] = random.nextInt(2);

            if (combs[i] == 1 && canPlay) {
                coins[i].setVisibility(View.VISIBLE);
            }
        }

        score = 0;
        rotation = 0.0f;

        coinTimer = new Timer();
        coinHandler = new Handler();

        coinTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                coinHandler.post(() -> {

                    for (ImageView coin: coins) {
                        if (canPlay) {
                            turn(coin);
                        }
                    }
                });
            }
        }, 20, 50);

        timer = new Timer();
        handler = new Handler();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (canPlay) {
                        move();
                    }
                });
            }
        }, 500, 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (((event.getX() > img.getX() - getResources().getDisplayMetrics().widthPixels / 50.0f && event.getX() < img.getX() + img.getWidth() + getResources().getDisplayMetrics().widthPixels / 50.0f) && (event.getY() > img.getY() - getResources().getDisplayMetrics().heightPixels / 20.0f && event.getY() < img.getY() + img.getHeight() + getResources().getDisplayMetrics().heightPixels / 20.0) && event.getX() - img.getWidth() / 2.0f > tunnelTop.getX() && event.getX() + img.getWidth() / 2.0f < tunnelTop.getX() + tunnelTop.getWidth()) && canPlay) {
            img.setX(event.getX() - img.getWidth() / 2.0f);
        }

        if (!(img.getX() > tunnelTop.getX())) {
            img.setX(img.getX() + 10);
        }

        if (!(img.getX() + img.getWidth() < tunnelTop.getX() + tunnelTop.getWidth())) {
            img.setX(img.getX() - 10);
        }

        return true;
    }

    public void turn(ImageView image) {
        rotation += 1.0f;
        image.setRotationY(rotation);
    }

    public void move() {
        arrow.setX(arrow.getX() + 0.5f);

        if (arrow.getX() + arrow.getWidth() >= tunnelTop.getX() + tunnelTop.getWidth() - 15) {
            arrow.setX(tunnelTop.getX() + 15);
        }

        img2.setX(img2.getX() + score / 100.0f + 1);
        img3.setX(img3.getX() - score / 100.0f - 1);

        if (img2.getX() + img2.getWidth() >= tunnelTop.getX() + tunnelTop.getWidth() - 10) {
            if (img2.getY() < switchPos.getY()) {
                img2.setY(tunnelBottom.getY() + 50);
            } else {
                img2.setY(tunnelTop.getY() + 50);
            }
            img2.setX(tunnelTop.getX() + 10);
        }

        if (img3.getX() <= tunnelTop.getX() + 10) {
            if (img3.getY() < switchPos.getY()) {
                img3.setY(tunnelBottom.getY() + 50);
            } else {
                img3.setY(tunnelTop.getY() + 50);
            }
            img3.setX(tunnelTop.getX() + tunnelTop.getWidth() - 10 - img3.getWidth());
        }

        if ((img2.getY() > switchPos.getY() && img.getY() > switchPos.getY()) || (img2.getY() < switchPos.getY() && img.getY() < switchPos.getY())) {
            if (img2.getX() + img2.getWidth() > img.getX() && img2.getX() < img.getX() + img.getWidth()) {
                onKill();
            }
        }

        if ((img3.getY() > switchPos.getY() && img.getY() > switchPos.getY()) || (img3.getY() < switchPos.getY() && img.getY() < switchPos.getY())) {
            if (img3.getX() < img.getX() + img.getWidth() && img3.getX() + img3.getWidth() > img.getX()) {
                onKill();
            }
        }

        for (ImageView coin: coins) {
            coinActivity(coin);
        }
    }

    public void coinActivity(final ImageView coin) {

        if (((coin.getY() > switchPos.getY() && img.getY() > switchPos.getY()) || (coin.getY() < switchPos.getY() && img.getY() < switchPos.getY())) && canPlay) {
            if (coin.getX() < img.getX() + img.getWidth() && coin.getX() + coin.getWidth() > img.getX() && isCollectable(coin)) {
                playSoundEffect(coinCollect);
                coin.setVisibility(View.INVISIBLE);
                scoreTxt.setText(String.valueOf(++score));
            }
        }
    }

    public void onKill() {
        playSoundEffect(endExplosion);

        timer.cancel();
        coinTimer.cancel();
        collectTimer.cancel();

        arrow.setX(tunnelTop.getX() + 15);
        arrow.setY(tunnelBottom.getY() + tunnelBottom.getHeight() + 20);
        arrow.setRotation(0);

        img.setX(tunnelTop.getX() + 10);
        img.setY(tunnelTop.getY());
        img2.setX(tunnelTop.getX() + 10);
        img2.setY(tunnelBottom.getY() + 50);
        img3.setX(tunnelTop.getX() + tunnelTop.getWidth() - 10 - img3.getWidth());
        img3.setY(tunnelTop.getY() + 50);

        img.setVisibility(View.INVISIBLE);
        img2.setVisibility(View.INVISIBLE);
        img3.setVisibility(View.INVISIBLE);
        arrow.setVisibility(View.INVISIBLE);
        switchPos.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);

        title.setVisibility(View.VISIBLE);
        logo.setVisibility(View.VISIBLE);
        start.setVisibility(View.VISIBLE);
        stats.setVisibility(View.VISIBLE);
        helpIcon.setVisibility(View.VISIBLE);
        settingsIcon.setVisibility(View.VISIBLE);

        for (ImageView coin: coins) {
            coin.setVisibility(View.GONE);
        }

        SharedPreferences sharedPreferences2 = getSharedPreferences("highscore", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        if (score > Integer.parseInt(highscoreTxt.getText().toString())) {
            editor.putString("high", "" + score);
        }
        editor.putString("total", Integer.toString(Integer.parseInt(totalTxt.getText().toString()) + score));
        editor.putString("games", Integer.toString(Integer.parseInt(gamesTxt.getText().toString()) + 1));
        double total = Integer.parseInt(totalTxt.getText().toString()) + score;
        double games = Integer.parseInt(gamesTxt.getText().toString()) + 1;
        editor.putString("avg", Double.toString(Math.round((total / games) * 100.0) / 100.0));
        editor.apply();

        setSprites();
        score = 0;
    }

    public boolean isCollectable(ImageView coin) {
        return coin.getVisibility() != View.INVISIBLE;
    }

    public void onSwitchPosClick(View view) {
        if (canPlay) {
            playSoundEffect(switchClick);
            if (img.getY() < switchPos.getY()) {
                img.setY(tunnelBottom.getY());
                arrow.setY(tunnelTop.getY() - 20 - arrow.getHeight());
                arrow.setRotation(180);
            } else {
                img.setY(tunnelTop.getY());
                arrow.setY(tunnelBottom.getY() + tunnelBottom.getHeight() + 20);
                arrow.setRotation(0);
            }
            //  sprite1.setX((float)(tunnelTop.getX() + random.nextInt(1738) + 50 - sprite1.getWidth() / 2.0f));
            img.setX(arrow.getX() - 15);
        }
    }

    public void onStatsClick(View view) {
        playSoundEffect(positiveClick);
        grid.setVisibility(View.VISIBLE);
        makeInvisible();
    }

    public void onCloseClick(View view) {
        playSoundEffect(negativeClick);
        grid.setVisibility(View.GONE);
        makeVisible();
    }

    public void onShipsClick(View view) {
        playSoundEffect(positiveClick);
        ships.setVisibility(View.VISIBLE);
        makeInvisible();
    }

    public void onClose2Click(View view) {
        playSoundEffect(negativeClick);
        ships.setVisibility(View.GONE);
        makeVisible();
    }

    public void onHelpClick(View view) {
        playSoundEffect(positiveClick);
        info.setVisibility(View.VISIBLE);
        closeInfo.setVisibility(View.VISIBLE);
        makeInvisible();
    }

    public void onCloseInfoClick(View view) {
        playSoundEffect(negativeClick);
        info.setVisibility(View.GONE);
        closeInfo.setVisibility(View.GONE);
        makeVisible();
    }

    public void onSettingsClick(View view) {
        playSoundEffect(positiveClick);
        settings.setVisibility(View.VISIBLE);
        makeInvisible();
    }

    public void onCloseSettingsClick(View view) {
        playSoundEffect(negativeClick);
        settings.setVisibility(View.GONE);
        makeVisible();
    }

    public void makeInvisible() {
        start.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        stats.setVisibility(View.INVISIBLE);
        helpIcon.setVisibility(View.INVISIBLE);
        settingsIcon.setVisibility(View.INVISIBLE);
        tunnelTop.setVisibility(View.INVISIBLE);
        tunnelBottom.setVisibility(View.INVISIBLE);
        for (ImageView end : ends) {
            end.setVisibility(View.INVISIBLE);
        }
    }

    public void makeVisible() {
        start.setVisibility(View.VISIBLE);
        logo.setVisibility(View.VISIBLE);
        stats.setVisibility(View.VISIBLE);
        helpIcon.setVisibility(View.VISIBLE);
        settingsIcon.setVisibility(View.VISIBLE);
        tunnelTop.setVisibility(View.VISIBLE);
        tunnelBottom.setVisibility(View.VISIBLE);
        for (ImageView end : ends) {
            end.setVisibility(View.VISIBLE);
        }
    }

    public void playSoundEffect(int soundID) {
        soundPool.play(soundID, soundVol, soundVol, 1, 0, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        musicPos = mediaPlayer.getCurrentPosition();
        mediaPlayer.release();
        mediaPlayer = null;
        soundPool.release();
        soundPool = null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences audio = getSharedPreferences("highscore", MODE_PRIVATE);

        soundSeek.setMax(100);
        musicSeek.setMax(100);
        soundSeek.setProgress((int) (audio.getFloat("soundVol", 0.5f) * 100));
        musicSeek.setProgress((int) (audio.getFloat("musicVol", 0.5f) * 100));

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setVolume(audio.getFloat("musicVol", 0.5f), audio.getFloat("musicVol", 0.5f));
        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(musicPos);
        mediaPlayer.start();

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(attributes)
                .build();
        coinCollect = soundPool.load(this, R.raw.coin_collect, 1);
        endExplosion = soundPool.load(this, R.raw.end_explosion, 1);
        negativeClick = soundPool.load(this, R.raw.negative_click, 1);
        positiveClick = soundPool.load(this, R.raw.positive_click, 1);
        switchClick = soundPool.load(this, R.raw.switch_click, 1);
        soundVol = audio.getFloat("soundVol", 0.5f);

        soundSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                soundVol = i / 100.0f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences audio1 = getSharedPreferences("highscore", MODE_PRIVATE);
                SharedPreferences.Editor audioEdit1 = audio1.edit();
                audioEdit1.putFloat("soundVol", seekBar.getProgress() / 100.0f);
                audioEdit1.apply();
            }
        });

        musicSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mediaPlayer.setVolume(i / 100.0f, i / 100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences audio2 = getSharedPreferences("highscore", MODE_PRIVATE);
                SharedPreferences.Editor audioEdit2 = audio2.edit();
                audioEdit2.putFloat("musicVol", seekBar.getProgress() / 100.0f);
                audioEdit2.apply();
            }
        });

        if (audio.getInt("flag", 0) == 0) {
            info.setVisibility(View.VISIBLE);
            closeInfo.setVisibility(View.VISIBLE);
            makeInvisible();
            SharedPreferences.Editor flagEdit = audio.edit();
            flagEdit.putInt("flag", 1);
            flagEdit.apply();
        }
    }
}
