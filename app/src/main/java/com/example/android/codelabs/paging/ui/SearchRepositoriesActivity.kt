/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.renderscript.Sampler
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android.codelabs.paging.Injection
import com.example.android.codelabs.paging.databinding.ActivitySearchRepositoriesBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SearchRepositoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchRepositoriesBinding
    private lateinit var viewModel: SearchRepositoriesViewModel
    private val adapter = ReposAdapter()

    private var searchJob: Job? = null

    private fun search(query: String) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
//            viewModel.searchRepo(query).collectLatest {
//                adapter.submitData(it)
//            }
            binding.list.apply {
                setContent {
//                    val pager = remember {
//                        Pager(
//                            PagingConfig(
//                                pageSize = myBackend.DataBatchSize,
//                                enablePlaceholders = true,
//                                maxSize = 200
//                            )
//                        ) { myBackend.getAllData() }
//                    }

//                    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
                    val lazyPagingItems = viewModel.searchRepo(query).collectAsLazyPagingItems()

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (lazyPagingItems.loadState.refresh == LoadState.Loading) {
                            item {
                                Text(
                                    text = "Waiting for items to load from the backend",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }


                        itemsIndexed(lazyPagingItems) { index, item ->
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                if (item is UiModel.RepoItem) {

                                    Card(modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                        .fillMaxWidth()
                                        .border(2.dp, Color.Magenta, RoundedCornerShape(4.dp))
                                        .clip(RoundedCornerShape(4.dp))
                                        .padding(4.dp)
                                        .clickable{
                                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.repo.url)))
                                        }
                                    ){
                                    Column{
                                        Text(item.repo.name, style = MaterialTheme.typography.button)
                                        Text(item.repo.description.toString(), style = MaterialTheme.typography.caption)
                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text("Stars: ${item.repo.stars}")
                                            Text("Language: ${item.repo.language}")
                                            Text("Forks: ${item.repo.forks}")
                                        }
                                    }
                                    }
                                }
//                                else if (item is UiModel.SeparatorItem){
////                                    Divider(color = Color.Green, thickness = 4.dp, startIndent = 16.dp)
//                                }
                            }
                        }

                        if (lazyPagingItems.loadState.append == LoadState.Loading) {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                    }
                }
            }
        }



        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchRepositoriesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // get the view model
        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(this)
        )[SearchRepositoriesViewModel::class.java]

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//        binding.list.addItemDecoration(decoration)
        // add dividers between RecyclerView's row items

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search(query)
        initSearch(query)
        binding.retryButton.setOnClickListener { adapter.retry() }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, binding.searchRepo.text.trim().toString())
    }

    private fun initAdapter() {
        val header = ReposLoadStateAdapter { adapter.retry() }

//        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
//            header = header,
//            footer = ReposLoadStateAdapter { adapter.retry() }
//        )

//        adapter.addLoadStateListener { loadState ->
//
//            // show empty list
//            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
//            showEmptyList(isListEmpty)
//
//            // Show a retry header if there was an error refreshing, and items were previously
//            // cached OR default to the default prepend state
//            header.loadState = loadState.mediator
//                ?.refresh
//                ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
//                ?: loadState.prepend
//
//            // Only show the list if refresh succeeds, either from the the local db or the remote.
//            binding.list.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
//            // Show loading spinner during initial load or refresh.
//            binding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
//            // Show the retry state if initial load or refresh fails and there are no items.
//            binding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0
//            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
//            val errorState = loadState.source.append as? LoadState.Error
//                ?: loadState.source.prepend as? LoadState.Error
//                ?: loadState.append as? LoadState.Error
//                ?: loadState.prepend as? LoadState.Error
//            errorState?.let {
//                Toast.makeText(
//                    this,
//                    "\uD83D\uDE28 Wooops ${it.error}",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
    }

    private fun initSearch(query: String) {
        binding.searchRepo.setText(query)

        binding.searchRepo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        binding.searchRepo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }

        // Scroll to top when the list is refreshed from network.
//        lifecycleScope.launch {
//            adapter.loadStateFlow
//                // Only emit when REFRESH LoadState for RemoteMediator changes.
//                .distinctUntilChangedBy { it.refresh }
//                // Only react to cases where Remote REFRESH completes i.e., NotLoading.
//                .filter { it.refresh is LoadState.NotLoading }
//                .collect { binding.list.scrollToPosition(0) }
//        }
    }

    private fun updateRepoListFromInput() {
        binding.searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }
}


