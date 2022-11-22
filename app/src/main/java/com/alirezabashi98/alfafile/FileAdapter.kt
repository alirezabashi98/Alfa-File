package com.alirezabashi98.alfafile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alirezabashi98.alfafile.databinding.ItemFileLinearBinding
import java.io.File
import java.net.URLConnection

class FileAdapter(val data: ArrayList<File>,val fileEvent: FileEvent) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    private lateinit var binding: ItemFileLinearBinding

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindViews(file: File) {

            binding.textView.text = file.name
            var fileType = ""

            if (file.isDirectory)
                binding.imageView.setImageResource(R.drawable.ic_folder)
            else {

                when {

                    isImage(file.path) -> {
                        binding.imageView.setImageResource(R.drawable.ic_image)
                        fileType = "image/*"
                    }
                    isVideo(file.path) -> {
                        binding.imageView.setImageResource(R.drawable.ic_video)
                        fileType = "video/*"
                    }
                    isZip(file.name) -> {
                        binding.imageView.setImageResource(R.drawable.ic_zip)
                        fileType = "application/zip"
                    }
                    else -> {
                        binding.imageView.setImageResource(R.drawable.ic_file)
                        fileType = "text/plain"
                    }

                }

            }

            itemView.setOnClickListener {

                if (file.isDirectory){
                    fileEvent.onFolderClicked(file.path)
                }else{
                    fileEvent.onFileClicked(file,fileType)
                }

            }

            itemView.setOnLongClickListener {
                fileEvent.onLongClick(file,adapterPosition)
                true
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ItemFileLinearBinding.inflate(layoutInflater)
        return FileViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: FileViewHolder, position: Int) =
        holder.bindViews(data[position])

    override fun getItemCount(): Int = data.size

    private fun isImage(path: String): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType.startsWith("image")
    }

    private fun isVideo(path: String): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType.startsWith("video")
    }

    private fun isZip(name: String): Boolean =
        name.contains(".zip") || name.contains(".rar")

    fun addNewFile(newFile: File){
        data.add(0,newFile)
        notifyItemInserted(0)
    }

    fun deleteFile(file: File,position: Int){
        data.remove(file)
        notifyItemRemoved(position)
    }

    interface FileEvent {

        fun onFileClicked(file: File, type: String)
        fun onFolderClicked(path: String)
        fun onLongClick(file: File,position: Int)

    }

}