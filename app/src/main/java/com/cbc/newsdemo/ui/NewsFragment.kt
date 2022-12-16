package com.cbc.newsdemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbc.newsdemo.R
import com.cbc.newsdemo.databinding.FragmentNewsBinding
import com.cbc.newsdemo.ui.adapters.NewsAdapter
import com.cbc.newsdemo.utils.Resource
import com.google.android.material.composethemeadapter.MdcTheme

@Suppress("DEPRECATION")
class NewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private var _binding: FragmentNewsBinding? = null

    private val binding get() = _binding!!

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

        newsAdapter = NewsAdapter(context)
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

        }
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_NewsFragment_to_articleFragment, bundle)
        }

        binding.swipeContainer.setOnRefreshListener {
            viewModel.getAllNews()
        }

        binding.composeView.setContent {
            MdcTheme {
                val showDialogState: Boolean by viewModel.showDialog.collectAsState()
                FilterAlertDialog(
                    show = showDialogState,
                    onDismiss = viewModel::onDialogDismiss,
                    onConfirm = viewModel::onDialogConfirm,
                    vm = viewModel
                )
            }
        }

        binding.fabFilter.setOnClickListener {
            viewModel.onFilterDialogClicked()
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

    @Composable
    fun TypeList(vm: NewsViewModel) {

        LazyColumn {
            items(vm.typeSet.size) { i ->
                val isChecked = remember { mutableStateOf(true) }
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        //isChecked.value = !isChecked.value
                    }) {
                    Checkbox(
                        checked = isChecked.value,
                        onCheckedChange = {
                            isChecked.value = it
                            if (it) {
                                vm.availableTypes.add(vm.typeSet.elementAt(i))
                                vm.availableTypes.add("story")
                            } else {
                                vm.availableTypes.remove(vm.typeSet.elementAt(i))
                            }
                        },
                        enabled = true
                    )
                    Text(text = vm.typeSet.elementAt(i), textAlign = TextAlign.Center)
                }
            }
        }
    }

    @Composable
    fun FilterAlertDialog(
        show: Boolean,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
        vm: NewsViewModel
    ) {
        if (show) {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(onClick = onConfirm)
                    { Text(text = getString(R.string.confirm)) }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss)
                    { Text(text = getString(R.string.cancel)) }
                },
                title = { Text(text = getString(R.string.show_by_type)) },
                text = {
                    TypeList(vm = vm)
                }
            )
        }

    }

}