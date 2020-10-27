package com.meliksahcakir.accountkeeper.addupdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.meliksahcakir.accountkeeper.MainActivity
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.friend.FriendAccountsViewModel
import com.meliksahcakir.accountkeeper.utils.ExitWithAnimation
import com.meliksahcakir.accountkeeper.utils.color
import com.meliksahcakir.accountkeeper.utils.startCircularReveal
import kotlinx.android.synthetic.main.activity_main.*

private const val ARG_EXIT_LOCATION = "exitLocation"
private const val ARG_EXIT_Y = "exit_y"

class AddUpdateAccountFragment : Fragment(), ExitWithAnimation {

    companion object {
        fun newInstance() = AddUpdateAccountFragment()
    }

    private val viewModel: FriendAccountsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            val location = args.getIntArray(ARG_EXIT_LOCATION)
            location?.let { it ->
                posX = it[0]
                posY = it[1]
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_update_account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(
            requireContext().color(R.color.colorPrimary),
            requireContext().color(R.color.colorBackground),
            posX,
            posY
        )
        setUpNavigation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun setUpNavigation() {
        val mainFab = (requireActivity() as MainActivity).mainFab
        mainFab.setOnClickListener {

        }
    }

    override var posX: Int? = null
    override var posY: Int? = null
    override fun isToBeExitedWithAnimation(): Boolean = true

}