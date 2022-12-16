package com.cbc.newsdemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbc.newsdemo.R
import com.cbc.newsdemo.databinding.FragmentSavedBinding
import com.cbc.newsdemo.ui.adapters.NewsAdapter
import com.google.android.material.snackbar.Snackbar

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        newsAdapter = NewsAdapter(context)
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            //addOnScrollListener(this@NewsFragment.scrollListener)

        }
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_SavedNewsFragment_to_articleFragment, bundle)
        }

        viewModel.getSavedArticle().observe(viewLifecycleOwner) { articles ->
            newsAdapter.differ.submitList(articles)
        }

        //swipe delete variable
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, //direction
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // swipe direction
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position= viewHolder.adapterPosition
                val article= newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                //execute undo Snackbar
                Snackbar.make(view, R.string.deleted, Snackbar.LENGTH_LONG).apply {
                    setAction(R.string.undo) {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvNews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}