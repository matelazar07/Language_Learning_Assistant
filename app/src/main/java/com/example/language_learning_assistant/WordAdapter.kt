package com.example.language_learning_assistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    private var wrdList: ArrayList<WordModel> = ArrayList()
    private var onClickItem: ((WordModel) -> Unit)? = null
    private var onClickDeleteItem: ((WordModel) -> Unit)? = null
    private var onClickEditItem: ((WordModel) -> Unit)? = null

    fun addItems(items: ArrayList<WordModel>) {
        this.wrdList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (WordModel) -> Unit) {
        this.onClickItem = callback
    }

    fun setOnClickDeleteItem(callback: (WordModel) -> Unit) {
        this.onClickDeleteItem = callback
    }

    fun setOnClickEditItem(callback: (WordModel) -> Unit) {
        this.onClickEditItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_items_wrd, parent, false)
        return WordViewHolder(view)
    }

    override fun getItemCount(): Int {
        return wrdList.size
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = wrdList[position]
        holder.bind(word, onClickDeleteItem, onClickEditItem)
    }

    fun filterList(filteredList: ArrayList<WordModel>) {
        wrdList = filteredList
        notifyDataSetChanged()
    }

    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val id: TextView = view.findViewById(R.id.tvId)
        private val article: TextView = view.findViewById(R.id.tvArticle)
        private val name: TextView = view.findViewById(R.id.tvWord)
        private val wordType: TextView = view.findViewById(R.id.tvType)
        private val meaning: TextView = view.findViewById(R.id.tvMeaning)
        private val plural: TextView = view.findViewById(R.id.tvPlural)
        private val btnDelete: Button = view.findViewById(R.id.btnDelete)
        private val btnEdit: Button = view.findViewById(R.id.btnEdit)

        fun bind(
            word: WordModel,
            onClickDeleteItem: ((WordModel) -> Unit)?,
            onClickEditItem: ((WordModel) -> Unit)?
        ) {
            id.text = word.id.toString()
            article.text = word.article
            name.text = word.name
            wordType.text = word.part_of_speech
            meaning.text = word.meaning
            plural.text = word.plural

            btnDelete.setOnClickListener {
                onClickDeleteItem?.invoke(word)
            }

            btnEdit.setOnClickListener {
                onClickEditItem?.invoke(word)
            }
        }
    }
}
