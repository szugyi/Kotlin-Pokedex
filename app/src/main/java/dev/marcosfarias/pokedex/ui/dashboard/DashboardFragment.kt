package dev.marcosfarias.pokedex.ui.dashboard

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import dev.marcosfarias.pokedex.R
import dev.marcosfarias.pokedex.utils.ImageLoadingListener
import dev.marcosfarias.pokedex.utils.PokemonColorUtil
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.app_bar
import kotlinx.android.synthetic.main.fragment_dashboard.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.image_shared_element_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postponeEnterTransition()
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity: AppCompatActivity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLike -> {
                    Toast.makeText(requireContext(), "Like pokemon", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> super.onOptionsItemSelected(menuItem)
            }
        }

        val id = checkNotNull(arguments?.getString("id"))
        val name = checkNotNull(arguments?.getString("name"))

        imageView.transitionName = name

        dashboardViewModel.getPokemonById(id).observe(viewLifecycleOwner, Observer { pokemonValue ->
            pokemonValue?.let { pokemon ->
                textViewID.text = pokemon.id
                textViewName.text = pokemon.name

                toolbar_layout.title = pokemon.name

                val color =
                    PokemonColorUtil(view.context).getPokemonColor(pokemon.typeofpokemon)
                coordinator.setBackgroundColor(color)
                toolbar_layout.contentScrim?.colorFilter =
                    PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                activity.window?.statusBarColor =
                    PokemonColorUtil(view.context).getPokemonColor(pokemon.typeofpokemon)

                pokemon.typeofpokemon?.getOrNull(0).let { firstType ->
                    textViewType1.text = firstType
                    textViewType1.isVisible = firstType != null
                }

                pokemon.typeofpokemon?.getOrNull(1).let { secondType ->
                    textViewType2.text = secondType
                    textViewType2.isVisible = secondType != null
                }

                Glide.with(view.context)
                    .load(pokemon.imageurl)
                    .listener(ImageLoadingListener {
                        startPostponedEnterTransition()
                    })
                    .into(imageView)

                val pager = viewPager
                val tabs = tabs
                pager.adapter =
                    ViewPagerAdapter(requireFragmentManager(), requireContext(), pokemon.id!!)
                tabs.setupWithViewPager(pager)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar_dashboard, menu)
    }
}
