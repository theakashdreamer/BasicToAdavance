package com.skysoftsolution.basictoadavance.teamModules
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivityTeamMyBinding
import com.skysoftsolution.basictoadavance.databinding.CustomAddUserLayoutBinding
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.teamModules.adapter.DistributorAdapter
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickSendPostion
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor
import com.skysoftsolution.basictoadavance.teamModules.viewModel.TeamViewModel
import com.skysoftsolution.basictoadavance.viewmodelfactory.MainViewModelFatcory
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator
import java.net.URLEncoder

class TeamMyActivity : AppCompatActivity() ,AdapterClickSendPostion{
    private lateinit var binding: ActivityTeamMyBinding
    private lateinit var teamViewModel: TeamViewModel
    private lateinit var adapter: DistributorAdapter
    private lateinit var dataAccessObj: DataAccessObj
    private var isAllFabsVisible: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamMyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        setupRecyclerView()
        observeDistributors()
        setupFABActions()
    }
    private fun setupViewModel() {
        dataAccessObj = DataBaseCreator.getInstance(this).dataAccessObj
        teamViewModel = ViewModelProvider(
            this, MainViewModelFatcory(MainRepository(dataAccessObj), application)
        )[TeamViewModel::class.java]

        teamViewModel.success.observe(this) { distributor ->
            Toast.makeText(this, "Distributor ${distributor.name} saved!", Toast.LENGTH_SHORT).show()
        }
        teamViewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupRecyclerView() {
        adapter = DistributorAdapter(emptyList(),this)
        binding.recyclerViewDistributors.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDistributors.adapter = adapter
    }

    private fun observeDistributors() {
        teamViewModel.getAllDistributors()?.observe(this) { distributors ->
            adapter.updateData(distributors)
        }
        teamViewModel.filteredDistributors.observe(this) { distributors ->
            adapter.updateData(distributors)
        }
    }

    private fun setupFABActions() {
        binding.addFab.setOnClickListener { toggleFabMenu() }
        binding.addPersonFab.setOnClickListener { showAddPersonDialog() }
        binding.addAlarmFab.setOnClickListener {
            Toast.makeText(this, "Alarm Added", Toast.LENGTH_SHORT).show()
        }
    }
    private fun toggleFabMenu() {
        isAllFabsVisible = !isAllFabsVisible
        val visibility = if (isAllFabsVisible) View.VISIBLE else View.GONE
        binding.addAlarmFab.visibility = visibility
        binding.addPersonFab.visibility = visibility
        binding.addAlarmActionText.visibility = visibility
        binding.addPersonActionText.visibility = visibility
    }
    private fun showAddPersonDialog() {
        val dialogBinding = CustomAddUserLayoutBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        alertDialog.show()
        dialogBinding.btnSubmit.setOnClickListener {
            val distributor = getDistributorFromInput(dialogBinding)
            if (distributor != null) {
                teamViewModel.insertDistributor(distributor)
                alertDialog.dismiss() // Dismiss only after successful input
            } else {
                Toast.makeText(this, "Please fill out all required fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getDistributorFromInput(dialogBinding: CustomAddUserLayoutBinding): Distributor? {
        val name = dialogBinding.etName.text.toString().trim()
        val mobileNumber = dialogBinding.etMobileNumber.text.toString().trim()
        val distributorId = dialogBinding.etDistributorId.text.toString().trim()
        val cityName = dialogBinding.etCityNAme.text.toString().trim()
        val spinner: Spinner = dialogBinding.spLevels
        val selectedLevel = spinner.selectedItem.toString()  // ✅ Directly fetch selected value
        return if (name.isNotEmpty() && mobileNumber.isNotEmpty() && distributorId.isNotEmpty()) {
            Distributor(
                name = name,
                mobileNumber = mobileNumber,
                cityName =cityName,
                selectedLevel = selectedLevel,  // ✅ Now correctly assigned
                distributorId = distributorId,
                status = "1"
            )
        } else {
            Toast.makeText(dialogBinding.root.context, "Please fill out all required fields!", Toast.LENGTH_SHORT).show()
            null
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_team_my, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search Users"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { teamViewModel.searchDistributors(query) }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { teamViewModel.searchDistributors(newText) }
                return false
            }
        })
        val switchItem = menu.findItem(R.id.action_switch)
        val switchView = switchItem.actionView as SwitchCompat
        switchView.thumbDrawable = ContextCompat.getDrawable(this, R.drawable.thumb_layerlist) // Custom thumb
        switchView.trackDrawable = ContextCompat.getDrawable(this, R.drawable.track_backgrounds) // Custom track
        switchView.isChecked = teamViewModel.isActiveFilter.value == true
        switchView.setOnCheckedChangeListener { _, isChecked ->
            teamViewModel.filterDistributorsByStatus(isChecked)
        }

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClickListenerDistributor(distributor: Distributor) {
        val options = arrayOf("WhatsApp", "Call")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an action")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openWhatsApp(distributor.mobileNumber) // Open WhatsApp
                1 -> makePhoneCall(distributor.mobileNumber) // Make a phone call
            }
        }
        builder.show()

    }

    override fun onSwitchStatusChanged(distributor: Distributor, isActive: Boolean) {
        Toast.makeText(this, "${distributor.name} status changed to ${if (isActive) "Active" else "Inactive"}", Toast.LENGTH_SHORT).show()
        teamViewModel.updateDistributorStatus(distributor)
    }

    private fun openWhatsApp(mobileNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val mobileNumberEncoded = URLEncoder.encode("+91$mobileNumber", "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$mobileNumberEncoded"
            intent.data = Uri.parse(url)
            intent.setPackage("com.whatsapp") // Ensure it opens in WhatsApp
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall(mobileNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$mobileNumber")
        startActivity(intent)
    }
}
