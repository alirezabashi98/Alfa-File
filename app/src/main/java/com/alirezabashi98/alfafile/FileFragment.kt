package com.alirezabashi98.alfafile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alirezabashi98.alfafile.databinding.DialogAddFileBinding
import com.alirezabashi98.alfafile.databinding.DialogAddFolderBinding
import com.alirezabashi98.alfafile.databinding.DialogDeleteItemBinding
import com.alirezabashi98.alfafile.databinding.FragmentFileBinding
import java.io.File

class FileFragment(val path: String) : Fragment(), FileAdapter.FileEvent {

    private lateinit var binding: FragmentFileBinding
    private lateinit var myAdapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileBinding.inflate(layoutInflater)


        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ourFile = File(path)
        binding.txtPath.text = ourFile.name + ">"

        if (ourFile.isDirectory) {

            val listOfFiles = arrayListOf<File>()
            listOfFiles.addAll(ourFile.listFiles()!!)

            myAdapter = FileAdapter(listOfFiles, this)
            binding.recyclerMain.adapter = myAdapter
            binding.recyclerMain.layoutManager = LinearLayoutManager(context)

            if (listOfFiles.size > 0) {


                showFile()
            } else {
                noDate()
            }

        }

        binding.btnAddFolder.setOnClickListener { createNewFolder() }

        binding.btnAddFile.setOnClickListener { createNewFile() }


    }

    private fun createNewFile() {
        val dialog = AlertDialog.Builder(context).create()

        val addFileBinding = DialogAddFileBinding.inflate(layoutInflater)
        addFileBinding.btnCreate.setOnClickListener {

            val nameOfNewFile = addFileBinding.edtAddFile.text

            val newFile = File(path + File.separator + nameOfNewFile)
            if (!newFile.exists()) {
                if (newFile.createNewFile()) {
                    myAdapter.addNewFile(newFile)
                    binding.recyclerMain.scrollToPosition(0)
                    showFile()
                } else
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }
        addFileBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.setView(addFileBinding.root)
        dialog.create()
        dialog.show()
    }

    private fun createNewFolder() {
        val dialog = AlertDialog.Builder(context).create()

        val addFolderBinding = DialogAddFolderBinding.inflate(layoutInflater)
        addFolderBinding.btnCreate.setOnClickListener {

            val nameOfNewFile = addFolderBinding.edtAddFolder.text

            val newFile = File(path + File.separator + nameOfNewFile)
            if (!newFile.exists()) {
                if (newFile.mkdir()) {
                    myAdapter.addNewFile(newFile)
                    binding.recyclerMain.scrollToPosition(0)
                    showFile()
                } else
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }
        addFolderBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.setView(addFolderBinding.root)
        dialog.create()
        dialog.show()
    }

    private fun showFile() {
        binding.recyclerMain.visibility = View.VISIBLE
        binding.imgNoData.visibility = View.GONE
    }

    private fun noDate() {
        binding.recyclerMain.visibility = View.GONE
        binding.imgNoData.visibility = View.VISIBLE
    }

    override fun onFileClicked(file: File, type: String) {

        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().packageName + ".provider",
                file
            )
            intent.setDataAndType(fileProvider, type)

        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
        intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        startActivity(intent)

    }

    override fun onFolderClicked(path: String) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main_container, FileFragment(path))
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onLongClick(file: File, position: Int) {
        val itemDeleteBinding = DialogDeleteItemBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext()).create()

        itemDeleteBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        itemDeleteBinding.btnDelete.setOnClickListener {

            if (file.exists())
                if (file.deleteRecursively())
                    myAdapter.deleteFile(file, position)

            dialog.dismiss()
        }

        dialog.setView(itemDeleteBinding.root)
        dialog.create()
        dialog.show()
    }

}