package com.skysoftsolution.basictoadavance.teamModules
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.skysoftsolution.basictoadavance.R
import com.skysoftsolution.basictoadavance.databinding.ActivityTeamMyBinding
import com.skysoftsolution.basictoadavance.databinding.CustomAddUserLayoutBinding
import com.skysoftsolution.basictoadavance.datasource.DataAccessObj
import com.skysoftsolution.basictoadavance.repository.MainRepository
import com.skysoftsolution.basictoadavance.taskDetails.entity.AddDailyRoutine
import com.skysoftsolution.basictoadavance.teamModules.adapter.DistributorAdapter
import com.skysoftsolution.basictoadavance.teamModules.callbacks.AdapterClickSendPostion
import com.skysoftsolution.basictoadavance.teamModules.entity.Distributor
import com.skysoftsolution.basictoadavance.teamModules.viewModel.TeamViewModel
import com.skysoftsolution.basictoadavance.utility.FirebaseFetchHelper
import com.skysoftsolution.basictoadavance.viewmodelfactory.MainViewModelFatcory
import com.skysoftsolution.thingisbeing.datasource.DataBaseCreator
import java.net.URLEncoder
import java.util.Collections.emptyList
class TeamMyActivity : AppCompatActivity() ,AdapterClickSendPostion{
    private lateinit var binding: ActivityTeamMyBinding
    private lateinit var teamViewModel: TeamViewModel
    private lateinit var adapter: DistributorAdapter
    private lateinit var dataAccessObj: DataAccessObj
    private var isAllFabsVisible: Boolean = false
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamMyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar("My Team", true)
        setupDraggableFabGroup()
        setupViewModel()
        setupRecyclerView()
        fetchDistributorsFromFirebase()
        observeDistributors()
        setupFABActions()

    }


    private fun setupActionBar(title: String, showBackButton: Boolean = false) {
        supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(showBackButton)
        }
    }

    private fun fetchDistributorsFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE  // Show progress
/*        db.collection("distributors")
            .get()
            .addOnSuccessListener { result ->
                teamViewModel.deleteAllDistributors()
                for (document in result) {
                    val distributor = document.toObject(Distributor::class.java)
                    teamViewModel.insertDistributor(distributor)
                }
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewDistributors.visibility = View.VISIBLE // Hide progress when done
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE  // Hide progress on failure
            }*/
        FirebaseFetchHelper.fetchDataIfNeeded<Distributor>(
            context = this,
            db = db,
            collectionName = "distributors",
            onInsert = { item -> teamViewModel.insertDistributor(item) },
            onDeleteAll = { teamViewModel.deleteAllDistributors() },
            onComplete = {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewDistributors.visibility = View.VISIBLE
            }
        )
    }


    private fun setupViewModel() {
        dataAccessObj = DataBaseCreator.getInstance(this).dataAccessObj
        teamViewModel = ViewModelProvider(
            this, MainViewModelFatcory(MainRepository(dataAccessObj), application)
        )[TeamViewModel::class.java]

        teamViewModel.success.observe(this) { distributor ->
            binding.recyclerViewDistributors.visibility = View.VISIBLE
        }
        teamViewModel.error.observe(this) { errorMsg ->
            Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
            binding.recyclerViewDistributors.visibility = View.GONE
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
        binding.addPersonFab.setOnClickListener { showAddPersonDialog() }
    }
    private fun toggleFabMenu() {
        isAllFabsVisible = !isAllFabsVisible
        val visibility = if (isAllFabsVisible) View.VISIBLE else View.GONE
        binding.addPersonFab.visibility = visibility
    }
    private fun setupDraggableFabGroup() {
        var dX = 0f
        var dY = 0f
        var startRawX = 0f
        var startRawY = 0f
        val clickThreshold = 10  // Movement tolerance for distinguishing click from drag

        binding.addFab.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    startRawX = event.rawX
                    startRawY = event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY

                    // Move main FAB
                    view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start()

                    // Move child FAB relative to the main FAB
                    binding.addPersonFab.animate()
                        .x(newX)
                        .y(newY - binding.addPersonFab.height - 40)  // Adjust vertical gap
                        .setDuration(0)
                        .start()
                }

                MotionEvent.ACTION_UP -> {
                    val deltaX = Math.abs(event.rawX - startRawX)
                    val deltaY = Math.abs(event.rawY - startRawY)

                    // Detect click
                    if (deltaX < clickThreshold && deltaY < clickThreshold) {

                        toggleFabMenu()  // Example: Open your sub-FABs
                    }
                }
            }
            true
        }
    }



    private fun makeFabDraggableAndClickable() {
        var dX = 0f
        var dY = 0f
        var downTime = 0L
        var startRawX = 0f
        var startRawY = 0f

        binding.addFab.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    downTime = System.currentTimeMillis()
                    startRawX = event.rawX
                    startRawY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                }
                MotionEvent.ACTION_UP -> {
                    val upTime = System.currentTimeMillis()
                    val movedX = Math.abs(event.rawX - startRawX)
                    val movedY = Math.abs(event.rawY - startRawY)
                    if (movedX < 10 && movedY < 10 && (upTime - downTime) < 300) {
                        binding.addFab.performClick()
                    }
                }
            }
            true
        }

        binding.addFab.setOnClickListener {
            toggleFabMenu()
        }
    }


    private fun showAddPersonDialog() {
        val dialogBinding = CustomAddUserLayoutBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        alertDialog.show()
        dialogBinding.btnSubmit.setOnClickListener {
            val distributor = getDistributorFromInput(dialogBinding)
            distributor?.let {
                db.collection("distributors")  // Name of collection
                    .add(it)
                    .addOnSuccessListener {
                        Toast.makeText(dialogBinding.root.context, "Distributor saved!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(dialogBinding.root.context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
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
            android.R.id.home -> {
                finish()  // Handles back button on ActionBar
                true
            }
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
