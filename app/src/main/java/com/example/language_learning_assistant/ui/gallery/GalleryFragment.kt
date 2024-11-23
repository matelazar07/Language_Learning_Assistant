package com.example.language_learning_assistant.ui.gallery

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.language_learning_assistant.R
import com.example.language_learning_assistant.SQLiteHelper
import com.example.language_learning_assistant.WordAdapter
import com.example.language_learning_assistant.WordModel
import com.example.language_learning_assistant.databinding.FragmentGalleryBinding
//import com.example.language_learning_assistant.databinding.FragmentVocabularyBinding
import java.io.File
import java.io.OutputStreamWriter
import java.util.Locale

class GalleryFragment : Fragment() {

//    asddasdasdas

    private var _binding: FragmentGalleryBinding? = null
//    private var _binding: FragmentVocabularyBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WordAdapter
    private lateinit var sqLiteHelper: SQLiteHelper
    private var wordList: ArrayList<WordModel> = ArrayList()
    private lateinit var edSearch: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
//        _binding = FragmentVocabularyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        edSearch = binding.edSearch
        val btnSearch = binding.btnSearch
        btnSearch.setOnClickListener {
            toggleSearchVisibility()
        }



        // Initialize adapter and RecyclerView
        adapter = WordAdapter()
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Get the words from arguments
        wordList = arguments?.getParcelableArrayList("WORD_LIST") ?: ArrayList()
        adapter.addItems(wordList) // Update adapter with the initial word list

        sqLiteHelper = SQLiteHelper(requireContext())
        getWords() // Load words from the database

        // Save button to save data to a file
        val btnSave = binding.btnSave
        btnSave.setOnClickListener {
            saveDataToTxtFile()
        }

        // Download button to download data
        val btnDownload = binding.btnDownload
        btnDownload.setOnClickListener {
            downloadData()
        }

        // Set up delete item click listener
        adapter.setOnClickDeleteItem { deleteWord(it.id) }

        // Setup search functionality
        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().toLowerCase(Locale.getDefault())
                val filteredList = wordList.filter { word ->
                    word.name.toLowerCase(Locale.getDefault()).contains(searchText) ||
                            word.meaning.toLowerCase(Locale.getDefault()).contains(searchText) ||
                            word.article.toLowerCase(Locale.getDefault()).contains(searchText) ||
                            word.plural.toLowerCase(Locale.getDefault()).contains(searchText) ||
                            word.part_of_speech.toLowerCase(Locale.getDefault()).contains(searchText)
                }
                adapter.filterList(ArrayList(filteredList))
            }
            override fun afterTextChanged(s: Editable?) {}
        })



        return root
    }

    private fun getWords() {
        wordList = sqLiteHelper.getAllWords() // Fetch all words from the database
        adapter.addItems(wordList) // Update the adapter with the new list
    }

    private fun toggleSearchVisibility() {
        edSearch.visibility = if (edSearch.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun deleteWord(id: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setMessage("Biztosan szeretnéd törölni az adatokat?")

        alertDialogBuilder.setPositiveButton("Nem") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Igen") { _, _ ->
            doDeleteWord(id)
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun doDeleteWord(id: Int) {
        val rowsAffected = sqLiteHelper.deleteWordsByID(id)
        Log.d("GalleryFragment", "Rows affected: $rowsAffected")
        if (rowsAffected > 0) {
            Toast.makeText(requireContext(), "Törlés sikeres", Toast.LENGTH_SHORT).show()
            getWords()
        } else {
            Toast.makeText(requireContext(), "Törlés sikertelen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToTxtFile(): Boolean {
        val dataToSave = wordList
        val fileName = "mentett_adatok.txt"

        return try {
            val fileOutputStream = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)

            for (item in dataToSave) {
                val line = "${item.article} - ${item.name} - ${item.part_of_speech} - ${item.plural} - ${item.meaning}\n"
                outputStreamWriter.write(line)
            }

            outputStreamWriter.close()
            Toast.makeText(requireContext(), "Adatok mentve: $fileName", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Hiba történt a mentés során.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            false
        }
    }

    private val STORAGE_PERMISSION_CODE = 100
    private val NOTIFICATION_PERMISSION_CODE = 101

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                STORAGE_PERMISSION_CODE
            )
        } else {
            proceedWithDownload()
        }
    }

    private fun downloadData() {
        checkAndRequestPermissions()
    }

    private fun proceedWithDownload() {
        val fileName = "mentett_adatok.txt"

        try {
            val sourceFile = File(requireContext().filesDir, fileName)
            if (!sourceFile.exists()) {
                saveDataToTxtFile()
                if (!sourceFile.exists()) {
                    Toast.makeText(requireContext(), "Hiba a fájl létrehozása során.", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Fájl letöltése")
                .setMessage("Szeretnéd letölteni a $fileName fájlt?")
                .setPositiveButton("Igen") { _, _ ->
                    try {
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        if (!downloadsDir.exists()) {
                            downloadsDir.mkdirs()
                        }

                        val destinationFile = File(downloadsDir, fileName)
                        sourceFile.inputStream().use { input ->
                            destinationFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }

                        showDownloadNotification(fileName)
                        Toast.makeText(requireContext(), "Fájl sikeresen letöltve", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Hiba a fájl mentése során: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Nem", null)
                .show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Hiba a letöltés során: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun showDownloadNotification(fileName: String) {
        val channelId = "download_channel"
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Downloads",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Download notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setContentTitle("Letöltés befejezve")
            .setContentText("$fileName sikeresen letöltve")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val intent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        } else {
            Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                setDataAndType(Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), "*/*")
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        builder.setContentIntent(pendingIntent)
        notificationManager.notify(1, builder.build())
    }

    @Deprecated("")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    proceedWithDownload()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Engedélyek szükségesek a letöltéshez",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
