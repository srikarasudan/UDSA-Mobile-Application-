package com.example.musicappf;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnPlayPause, btnPrevious, btnNext, btnShuffle, btnRepeat;
    private SeekBar songProgress;
    private TextView currentSongTitle, currentArtist, currentTime, totalTime;
    private ListView songsListView;

    // Media Player
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private boolean isPlaying = false;

    // Song Data
    private ArrayList<Song> songs;
    private int currentSongIndex = 0;

    // Song class to hold song information
    private class Song {
        String title;
        String artist;
        int albumArt;
        int audioResource;

        Song(String title, String artist, int albumArt, int audioResource) {
            this.title = title;
            this.artist = artist;
            this.albumArt = albumArt;
            this.audioResource = audioResource;
        }

        @Override
        public String toString() {
            return title + "\n" + artist;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initializeViews();
        initializeSongs();
        setupListView();
        setupMediaPlayer();
        setupButtonListeners();
    }

    private void initializeViews() {
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnRepeat = findViewById(R.id.btnRepeat);
        songProgress = findViewById(R.id.songProgress);
        currentSongTitle = findViewById(R.id.currentSongTitle);
        currentArtist = findViewById(R.id.currentArtist);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        songsListView = findViewById(R.id.songsListView);
    }

    private void initializeSongs() {
        songs = new ArrayList<>();

        // Add 6 sample songs with different album arts
        songs.add(new Song("Innerbloom", "RÜFÜS DU SOL", R.drawable.album_cover1, R.raw.song1));
        songs.add(new Song("Come & Get It", "Selena Gomez", R.drawable.album_cover2, R.raw.song2));
        songs.add(new Song("Californication", "Red Hot Chili Peppers", R.drawable.album_cover3, R.raw.song3));
        songs.add(new Song("So Baby", "Anirudh Ravichander", R.drawable.album_cover4, R.raw.song4));
        songs.add(new Song("First Woe", "Øfdream", R.drawable.album_cover5, R.raw.song5));
        songs.add(new Song("Vibes", "ZAYN ", R.drawable.album_cover6, R.raw.song6));

        // Set default display
        updateSongDisplay();
    }

    private void setupListView() {
        ArrayAdapter<Song> adapter = new ArrayAdapter<Song>(this,
                android.R.layout.simple_list_item_1, songs) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setTextSize(16);
                return view;
            }
        };

        songsListView.setAdapter(adapter);

        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playSong(position);
            }
        });
    }

    private void setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, songs.get(currentSongIndex).audioResource);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextSong();
            }
        });

        // Set up seekbar
        songProgress.setMax(mediaPlayer.getDuration());
        totalTime.setText(formatTime(mediaPlayer.getDuration()));
    }

    private void setupButtonListeners() {
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicPlayerActivity.this, "Shuffle activated!", Toast.LENGTH_SHORT).show();
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicPlayerActivity.this, "Repeat mode changed", Toast.LENGTH_SHORT).show();
            }
        });

        songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playSong(int position) {
        currentSongIndex = position;

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, songs.get(currentSongIndex).audioResource);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextSong();
            }
        });

        updateSongDisplay();

        if (isPlaying) {
            mediaPlayer.start();
            updateSeekBar();
        }

        Toast.makeText(this, "Now playing: " + songs.get(currentSongIndex).title, Toast.LENGTH_SHORT).show();
    }

    private void togglePlayPause() {
        if (isPlaying) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            updateSeekBar();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % songs.size();
        playSong(currentSongIndex);
    }

    private void playPreviousSong() {
        currentSongIndex = (currentSongIndex - 1 + songs.size()) % songs.size();
        playSong(currentSongIndex);
    }

    private void updateSongDisplay() {
        Song currentSong = songs.get(currentSongIndex);
        currentSongTitle.setText(currentSong.title);
        currentArtist.setText(currentSong.artist);

        // Update album art (you'll need to create these drawables)
        // albumArt.setImageResource(currentSong.albumArt);

        if (mediaPlayer != null) {
            songProgress.setMax(mediaPlayer.getDuration());
            totalTime.setText(formatTime(mediaPlayer.getDuration()));
        }
    }

    private String formatTime(int milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void updateSeekBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                songProgress.setProgress(currentPosition);
                currentTime.setText(formatTime(currentPosition));
                handler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateTimeTask);
    }
}