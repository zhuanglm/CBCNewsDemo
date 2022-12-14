package com.cbc.newsdemo.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.cbc.newsdemo.R
import com.cbc.newsdemo.databinding.FragmentArticleBinding
import com.cbc.newsdemo.ui.MainActivity
import com.cbc.newsdemo.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article){
    lateinit var viewModel: NewsViewModel
    private var _binding: FragmentArticleBinding? = null

    private val binding get() = _binding!!

    private val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as MainActivity).viewModel

        val article = args.article
        binding.webView.apply {
            webViewClient= WebViewClient()
            loadUrl(article.url.toString())
        }

        //save article
        binding.fab.setOnClickListener{
            //viewModel.saveArticle(article)
            Snackbar.make(view, " Article Saved Successfully! ", Snackbar.LENGTH_SHORT).show()
        }


    }
}