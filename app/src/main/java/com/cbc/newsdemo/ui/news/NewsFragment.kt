package com.cbc.newsdemo.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbc.newsdemo.R
import com.cbc.newsdemo.databinding.FragmentNewsBinding
import com.cbc.newsdemo.ui.MainActivity
import com.cbc.newsdemo.ui.NewsViewModel
import com.cbc.newsdemo.ui.adapters.NewsAdapter
import com.cbc.newsdemo.utils.Constants.Companion.QUERY_PAGE_SIZE
import androidx.navigation.fragment.findNavController
import com.cbc.newsdemo.utils.Resource

class NewsFragment : Fragment() {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    var queryPageSize = 0

    private var _binding: FragmentNewsBinding? = null

    private val binding get() = _binding!!
    val TAG= "NewsFragment"
    var isLoading= false
    var isLastPage= false
    var isScrolling= false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        newsAdapter= NewsAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsFragment.scrollListener)

        }
        newsAdapter.setOnItemClickListener {
            val bundle= Bundle().apply{
                //adding article to the bundle
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_NewsFragment_to_articleFragment, bundle)
        }

        viewModel.news.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    //check null
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        queryPageSize = newsResponse.articles.size
                        val totalPages = newsResponse.totalResults / queryPageSize + 2
                        isLastPage = viewModel.newsPagination == totalPages
                        if (isLastPage) {
                            binding.rvNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun hideProgressBar(){
        _binding?.pbPagination?.visibility= View.INVISIBLE
        isLoading= false
    }
    private fun showProgressBar(){
        _binding?.pbPagination?.visibility= View.VISIBLE
        isLoading= true
    }

    val scrollListener= object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //manually calculating payout numbers for pagination
            val layoutManager= recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount= layoutManager.childCount
            val totalItemCount= layoutManager.itemCount

            val isAtLastItem= firstVisibleItemPosition+ visibleItemCount >= totalItemCount
            val isNotAtBeginning= firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible= totalItemCount >= queryPageSize

            val shouldPaginate = (!isLoading && !isLastPage) && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                viewModel.getAllNews("ca")
                isScrolling= false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling= true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}