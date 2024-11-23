package com.example.language_learning_assistant.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.language_learning_assistant.R
import com.example.language_learning_assistant.SQLiteHelper
import com.example.language_learning_assistant.WordAdapter
import com.example.language_learning_assistant.WordModel
import com.example.language_learning_assistant.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var edArticle: EditText
    private lateinit var edName: EditText
    private lateinit var edMeaning: EditText
    private lateinit var edPlural: EditText
    private lateinit var edPartofspeech: EditText
    private lateinit var btnAdd: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var sqLiteHelper: SQLiteHelper
    private var adapter: WordAdapter? = null
    private var wrd: WordModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initView(root)
        initRecyclerView()
        sqLiteHelper = SQLiteHelper(requireContext())

        btnAdd.setOnClickListener { addWords() }



        adapter = WordAdapter()
        adapter?.setOnClickItem {
            Toast.makeText(requireContext(), it.name, Toast.LENGTH_SHORT).show()
            edArticle.setText(it.article)
            edName.setText(it.name)
            edPartofspeech.setText(it.part_of_speech)
            edMeaning.setText(it.meaning)
            edPlural.setText(it.plural)
            wrd = it
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return root
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WordAdapter()
        recyclerView.adapter = adapter
    }

    private fun addWords() {
        val article = edArticle.text.toString()
        val name = edName.text.toString()
        val  part_of_speech = edPartofspeech.text.toString()
        val meaning = edMeaning.text.toString()
        val plural = edPlural.text.toString()
        if (name.isEmpty() || meaning.isEmpty()) {
            Toast.makeText(requireContext(), "Kérjük írja be a kötelező mezőket", Toast.LENGTH_SHORT).show()
            edName.error = "Kötelező mező"
            edMeaning.error = "Kötelező mező"
            edName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            edMeaning.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            edName.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            edMeaning.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            edName.error = null
            edMeaning.error = null

            val wrd = WordModel(article = article, name = name, part_of_speech = part_of_speech, meaning = meaning, plural = plural)
            val status = sqLiteHelper.insertWords(wrd)

            if (status > -1) {
                Toast.makeText(requireContext(), "Szó felvétel sikeres", Toast.LENGTH_SHORT).show()
                clearEditText()

                getWords()
            } else {
                Toast.makeText(requireContext(), "Szó nincs felvétel", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWords() {
        val wrdList = sqLiteHelper.getAllWords()
        adapter?.addItems(wrdList)
    }

    private fun clearEditText() {
        edArticle.setText("")
        edName.setText("")
        edPartofspeech.setText("")
        edMeaning.setText("")
        edPlural.setText("")
    }

    private fun initView(view: View) {
        edName = view.findViewById(R.id.edName)
        edPartofspeech = view.findViewById(R.id.edWordType)
        edArticle = view.findViewById(R.id.edArticle)
        edPlural = view.findViewById(R.id.edPlural)
        edMeaning = view.findViewById(R.id.edMeaning)
        btnAdd = view.findViewById(R.id.btnAdd)
        recyclerView = view.findViewById(R.id.recyclerView)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
