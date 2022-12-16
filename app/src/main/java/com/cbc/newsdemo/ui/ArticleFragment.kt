package com.cbc.newsdemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.cbc.newsdemo.R
import com.cbc.newsdemo.databinding.FragmentArticleBinding
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article){
    private lateinit var viewModel: NewsViewModel
    private var _binding: FragmentArticleBinding? = null

    private val binding get() = _binding!!

    private val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as MainActivity).viewModel

        val article = args.article
        binding.webView.apply {
            webViewClient= WebViewClient()
            loadUrl(article.typeAttributes?.url.toString())
        }

        binding.fab.setOnClickListener{
            viewModel.saveArticle(article)
            Snackbar.make(view, getString(R.string.saved), Snackbar.LENGTH_SHORT).show()
        }

        article.id?.let {
            viewModel.getSavedArticleBy(it).observe(viewLifecycleOwner) { articles ->
                    if (articles.isNotEmpty())
                        binding.fab.visibility = View.GONE
                }
        }

    }
}