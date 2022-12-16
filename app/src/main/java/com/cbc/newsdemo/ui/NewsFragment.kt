package com.cbc.newsdemo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbc.newsdemo.R
import com.cbc.newsdemo.databinding.FragmentNewsBinding
import com.cbc.newsdemo.ui.adapters.NewsAdapter
import com.cbc.newsdemo.utils.Resource

class NewsFragment : Fragment() {
    lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    var queryPageSize = 0

    private var _binding: FragmentNewsBinding? = null

    private val binding get() = _binding!!
    private val TAG= "NewsFragment"
    var isLoading= false
    var isLastPage= false
    //var isScrolling= false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        newsAdapter= NewsAdapter(context)
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            //addOnScrollListener(this@NewsFragment.scrollListener)

        }
        newsAdapter.setOnItemClickListener {
            val bundle= Bundle().apply{
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_NewsFragment_to_articleFragment, bundle)
        }

        binding.swipeContainer.setOnRefreshListener {
            //newsAdapter.differ.submitList(null)
            viewModel.getAllNews()
        }

        viewModel.news.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.swipeContainer.isRefreshing = false
                    //check null
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.toList())
                    }
                }
                is Resource.Error -> {
                    binding.swipeContainer.isRefreshing = false
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    binding.swipeContainer.isRefreshing = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}