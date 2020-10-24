package com.meliksahcakir.accountkeeper.addupdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.meliksahcakir.accountkeeper.R
import com.meliksahcakir.accountkeeper.utils.ExitWithAnimation

private const val ARG_EXIT_X = "exit_x"
private const val ARG_EXIT_Y = "exit_y"

class AddUpdateAccountFragment : Fragment(), ExitWithAnimation {

    companion object {
        fun newInstance() = AddUpdateAccountFragment()
    }

    private lateinit var viewModel: AddUpdateAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            posX = it.getInt(ARG_EXIT_X)
            posY = it.getInt(ARG_EXIT_Y)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_update_account_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddUpdateAccountViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override var posX: Int? = null
    override var posY: Int? = null
    override fun isToBeExitedWithAnimation(): Boolean = true

}