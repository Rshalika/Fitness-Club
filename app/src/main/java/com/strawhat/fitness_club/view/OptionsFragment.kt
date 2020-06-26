package com.strawhat.fitness_club.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.strawhat.fitness_club.R
import kotlinx.android.synthetic.main.fragment_options.*


class OptionsFragment : BottomSheetDialogFragment() {


    override fun getTheme() = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        close_button.setOnClickListener {
            dismiss()
        }
        assign_permissions_button.setOnClickListener {
            Toast.makeText(requireContext(), R.string.assign_permission, Toast.LENGTH_SHORT).show()
            dismiss()
        }
        delete_members_button.setOnClickListener {
            Toast.makeText(requireContext(), R.string.remove_members, Toast.LENGTH_SHORT).show()
            dismiss()
        }
        leave_group_button.setOnClickListener {
            Toast.makeText(requireContext(), R.string.leave_group, Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = OptionsFragment()
    }
}