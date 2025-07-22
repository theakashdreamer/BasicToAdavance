package com.skysoftsolution.basictoadavance.musicPlayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.musicPlayer.entity.Song

class SongAdapter(
    private val songs: List<Song>,
    private val onSongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.songTitle)
        val artist: TextView = view.findViewById(R.id.songArtist)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.title.text = song.title
        holder.artist.text = song.artist
        holder.itemView.setOnClickListener {
            onSongClick(song)
            // Show BottomSheetDialogFragment
           /* val bottomSheet = SongDetailBottomSheet.newInstance(song.title, song.artist)
            bottomSheet.show(fragmentManager, "SongDetailBottomSheet")*/
        }
    }

    override fun getItemCount() = songs.size
}
