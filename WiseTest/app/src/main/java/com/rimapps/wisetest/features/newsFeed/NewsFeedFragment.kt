package com.rimapps.wisetest.features.newsFeed

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rimapps.wisetest.R
import com.rimapps.wisetest.data.NewsArticle
import com.rimapps.wisetest.databinding.FragmentNewsFeedBinding
import com.rimapps.wisetest.util.Resource
import com.rimapps.wisetest.util.exhaustive
import com.rimapps.wisetest.util.showsnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NewsFeedFragment : Fragment(R.layout.fragment_news_feed),NewsFeedListAdapter.OnItemClickListener {
    private val viewModel: NewsFeedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentNewsFeedBinding.bind(view)
        val newsFeedListAdapter = NewsFeedListAdapter(this)

        binding.apply {
            rvNewsFeed.apply {
                adapter = newsFeedListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.newsFeed.collect{
                    val result = it?: return@collect

                    swipeRefreshLayout.isRefreshing = result is Resource.Loading
                    rvNewsFeed.isVisible = !result.data.isNullOrEmpty()
                    tvError.isVisible = result.error !=null && result.data.isNullOrEmpty()
                    tvError.text = getString(
                        R.string.could_not_refresh,
                        result.error?.localizedMessage
                            ?:getString(R.string.unknown_error_occured)
                    )


                    newsFeedListAdapter.submitList(result.data)
                    newsFeedListAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
            }
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.onManualRefresh()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect{
                    when (it){
                        is NewsFeedViewModel.Event.ShowErrorMessage -> showsnackBar(
                            getString(R.string.could_not_refresh,
                            it.error.localizedMessage?:getString(R.string.unknown_error_occured))
                        )
                    }.exhaustive
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.onStart()
    }


    override fun onItemClick(newsArticle: NewsArticle) {
        val clickAction = NewsFeedFragmentDirections.actionFeedFragmentToDetailsFragment(newsArticle)
        findNavController().navigate(clickAction)

    }
}