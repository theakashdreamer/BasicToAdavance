package com.skysoftsolution.basictoadavance.musicPlayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivityMusicPlayerBinding
import com.skysoftsolution.basictoadavance.musicPlayer.entity.Song
import kotlin.random.Random
import com.bumptech.glide.request.transition.Transition
class MusicPlayerActivity1 : AppCompatActivity() {
    private lateinit var binding: ActivityMusicPlayerBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var songAdapter: SongAdapter
    private val handler = Handler()
    private var songList = mutableListOf<Song>()
    private var shuffledList = mutableListOf<Song>()
    private var currentSongIndex = 0
    private var isLooping = false
    private var isShuffling = false
    private var isPlaying1 = false
    private val REQUEST_CODE = 100
    private var currentSongPosition: Int = 0
    private  var vibrantColor: Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        songAdapter = SongAdapter(songList) {
            song -> playSong(song)
            showBottomSheet(song.title,song.artist);
        }
        binding.recyclerView.adapter = songAdapter
        binding.llForInclude.songTitleBottom.requestFocus()
        binding.llForInclude.viewPlayMusicTitleBottom.setOnClickListener {
            binding.PlayContainer.visibility=View.VISIBLE
            binding.llForSongsList.visibility=View.GONE
            hideBottomSheet()
            if (vibrantColor != 0) {
                if (binding.PlayContainer.visibility == View.VISIBLE) {
                    window.statusBarColor = vibrantColor
                }
            }
            Toast.makeText(this@MusicPlayerActivity1, binding.llForInclude.songTitleBottom.text.toString(), Toast.LENGTH_SHORT).show()
        }
        if (hasFilePermission()) {
            loadSongs()
        } else {
            requestFilePermission()
        }
        binding.llForPlayMusic.btnPlay.setOnClickListener {
            if (::mediaPlayer.isInitialized) {
                if (isPlaying1) {
                    currentSongPosition = mediaPlayer.currentPosition
                    mediaPlayer.pause()
                    isPlaying1 = false
                    binding.llForPlayMusic.btnPlay.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                        R.drawable.baseline_play_circle_filled_24));
                    binding.llForInclude.playButtonbottom.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                        R.drawable.baseline_play_circle_filled_24));
              //      binding.llForPlayMusic.btnPlay.text = "Play"
                } else {

                    mediaPlayer.seekTo(currentSongPosition)
                    mediaPlayer.start()
                    isPlaying1 = true
                    binding.llForPlayMusic.btnPlay.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                        R.drawable.baseline_pause_circle_outline_24));
                    binding.llForInclude.playButtonbottom.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                        R.drawable.baseline_pause_circle_outline_24));
              //      binding.llForPlayMusic.btnPlay.text = "Pause"
                    updateSeekBar()
                }
            } else {
                val song = if (isShuffling) shuffledList[currentSongIndex] else songList[currentSongIndex]
                playSong(song)
                binding.llForPlayMusic.btnPlay.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.baseline_pause_circle_outline_24));
                binding.llForInclude.playButtonbottom.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.baseline_pause_circle_outline_24));
             //   binding.llForPlayMusic.btnPlay.text = "Pause"
            }
        }

        binding.llForInclude.playButtonbottom.setOnClickListener {
            if (::mediaPlayer.isInitialized) {
                if (isPlaying1) {
                    currentSongPosition = mediaPlayer.currentPosition
                    mediaPlayer.pause()
                    isPlaying1 = false
                    binding.llForInclude.playButtonbottom.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                        R.drawable.baseline_play_circle_filled_24));
                    binding.llForPlayMusic.btnPlay.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                        R.drawable.baseline_play_circle_filled_24));
                    //      binding.llForPlayMusic.btnPlay.text = "Play"
                } else {

                    mediaPlayer.seekTo(currentSongPosition)
                    mediaPlayer.start()
                    isPlaying1 = true
                    binding.llForInclude.playButtonbottom.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                        R.drawable.baseline_pause_circle_outline_24));
                    binding.llForPlayMusic.btnPlay.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                        R.drawable.baseline_pause_circle_outline_24));
                    //      binding.llForPlayMusic.btnPlay.text = "Pause"
                    updateSeekBar()
                }
            } else {
                val song = if (isShuffling) shuffledList[currentSongIndex] else songList[currentSongIndex]
                playSong(song)
                binding.llForPlayMusic.btnPlay.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.baseline_pause_circle_outline_24));
                binding.llForInclude.playButtonbottom.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.baseline_pause_circle_outline_24));
                //   binding.llForPlayMusic.btnPlay.text = "Pause"
            }
        }


        binding.llForPlayMusic.btnNext.setOnClickListener {
            if (isShuffling) {

                currentSongIndex = Random.nextInt(shuffledList.size)
                playSong(shuffledList[currentSongIndex])
            } else {
                if (currentSongIndex < songList.size - 1) {
                    currentSongIndex++
                    playSong(songList[currentSongIndex])
                } else if (isLooping) {
                    currentSongIndex = 0
                    playSong(songList[currentSongIndex])
                } else {
                    Toast.makeText(this, "This is the last song.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.llForPlayMusic.btnPrevious.setOnClickListener {
            if (isShuffling) {
                currentSongIndex = Random.nextInt(shuffledList.size)
                playSong(shuffledList[currentSongIndex])
            } else {
                if (currentSongIndex > 0) {
                    currentSongIndex--
                    playSong(songList[currentSongIndex])
                } else if (isLooping) {
                    currentSongIndex = songList.size - 1
                    playSong(songList[currentSongIndex])
                } else {
                    Toast.makeText(this, "This is the first song.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.llForPlayMusic.btnRepeat.setOnClickListener {
            isLooping = !isLooping
            if (isLooping) {
                binding.llForPlayMusic.btnRepeat.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.ic_repeat_off));
                ///binding.llForPlayMusic.btnRepeat.text = "Looping: ON"
                mediaPlayer.isLooping = true
            } else {
                ///binding.llForPlayMusic.btnRepeat.text = "Looping: OFF"
                mediaPlayer.isLooping = false
                binding.llForPlayMusic.btnRepeat.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.ic_repeat));
            }
        }

        binding.llForPlayMusic.btnShuffle.setOnClickListener {
            isShuffling = !isShuffling
            if (isShuffling) {
                binding.llForPlayMusic.btnShuffle.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.baseline_shuffle_off_24));
            ///    binding.llForPlayMusic.btnShuffle.text = "Shuffle: ON"
                shuffleSongs()
            } else {
                binding.llForPlayMusic.btnShuffle.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.baseline_shuffle_24));
               // binding.llForPlayMusic.btnShuffle.text = "Shuffle: OFF"
                shuffledList.clear()
                shuffledList.addAll(songList)
            }
        }

        binding.llForPlayMusic.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }

    private fun hasFilePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        }
    }
    @SuppressLint("Recycle")
    private fun loadSongs() {
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use { it ->
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val pathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val path = it.getString(pathColumn)
                val albumId = it.getLong(albumIdColumn)
                val albumName = getAlbumName(this@MusicPlayerActivity1, albumId)
                val song = Song(title, artist, path, albumId)
                songList.add(song)
            }
        }

        shuffledList.clear()
        shuffledList.addAll(songList.shuffled())

        songAdapter.notifyDataSetChanged()
    }

    fun getAlbumName(context: Context, albumId: Long): String {
        val albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Albums.ALBUM)
        val selection = "${MediaStore.Audio.Albums._ID}=?"
        val selectionArgs = arrayOf(albumId.toString())

        context.contentResolver.query(albumUri, projection, selection, selectionArgs, null).use { cursor ->
            cursor?.moveToFirst()?.let {
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
                return cursor.getString(albumColumn)
            }
        }
        return "Unknown Album"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs()
            } else {
                Toast.makeText(this, "Permission denied. Cannot access media files.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacksAndMessages(null)
    }

    private fun playSong(song: Song) {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(song.path)
            setOnPreparedListener {
                it.start()
                binding.llForPlayMusic.seekBar.max = mediaPlayer.duration
                updateSeekBar()
                isPlaying1 = true
                binding.llForPlayMusic.btnPlay.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MusicPlayerActivity1,
                        R.drawable.baseline_pause_circle_outline_24
                    )
                )
                binding.llForInclude.playButtonbottom.setImageDrawable(ContextCompat.getDrawable(this@MusicPlayerActivity1,
                    R.drawable.baseline_pause_circle_outline_24));
            }

            setOnCompletionListener {
                if (isShuffling) {
                    currentSongIndex = Random.nextInt(shuffledList.size)
                    playSong(shuffledList[currentSongIndex])
                } else {
                    if (currentSongIndex < songList.size - 1) {
                        currentSongIndex++
                        playSong(songList[currentSongIndex])
                    } else if (isLooping) {
                        currentSongIndex = 0
                        playSong(songList[currentSongIndex])
                    } else {
                        Toast.makeText(
                            this@MusicPlayerActivity1,
                            "This is the last song.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            prepareAsync()
        }

        // Set song title and album art
        binding.llForPlayMusic.songTitle.text = song.title



        val albumArtUri = Uri.parse("content://media/external/audio/albumart/${song.album}")
        Glide.with(this)
            .asBitmap()
            .load(albumArtUri)
            .placeholder(R.drawable.placeholder_image) // Optional placeholder image
            .error(R.drawable.error_image) // Optional error image
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->
                         vibrantColor = palette?.getVibrantColor(0)!!
                    }
                }
            })
        Glide.with(this)
            .load(albumArtUri)
            .placeholder(R.drawable.placeholder_image) // Optional placeholder image
            .error(R.drawable.error_image) // Optional error image
            .into(binding.llForPlayMusic.albumArt) // Replace with your ImageView reference
        Glide.with(this)
            .asBitmap()
            .load(albumArtUri)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.llForPlayMusic.llForMusicColour.setBackground(BitmapDrawable(resources, resource))
                }
            })
    }


    private fun updateSeekBar() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            binding.llForPlayMusic.seekBar.progress = mediaPlayer.currentPosition
            binding.llForPlayMusic.startTime.text = "${formatTime(mediaPlayer.currentPosition)}"
            binding.llForPlayMusic.endTime.text = "${formatTime(mediaPlayer.duration)}"
            handler.postDelayed({ updateSeekBar() }, 1000)
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    private fun shuffleSongs() {
        shuffledList.shuffle()
    }

    private fun showBottomSheet(title: String, artist: String) {
        binding.llForInclude.songTitleBottom.text = title
        binding.llForInclude.songArtistBottom.text = artist
        binding.bottomSheetContainer.visibility = View.VISIBLE
        binding.bottomSheetContainer.animate()
            .translationY(0f)
            .setDuration(300)
            .start()
    }

    private fun hideBottomSheet() {
        binding.bottomSheetContainer.animate()
            .translationY(binding.bottomSheetContainer.height.toFloat())
            .setDuration(300)
            .withEndAction {
                binding.bottomSheetContainer.visibility = View.GONE
            }
            .start()
    }

    override fun onBackPressed() {
        if (binding.PlayContainer.visibility == View.VISIBLE) {
            binding.PlayContainer.visibility = View.GONE
            binding.llForSongsList.visibility = View.VISIBLE
            binding.bottomSheetContainer.visibility = View.VISIBLE
            binding.bottomSheetContainer.animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        } else {
            super.onBackPressed()
        }
    }
}
