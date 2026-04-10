package dev.aira.saudeEmRota.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dev.aira.saudeEmRota.R
import dev.aira.saudeEmRota.databinding.FragmentMapBinding
import dev.aira.saudeEmRota.db.UsfRepository
import dev.aira.saudeEmRota.model.Usf
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class MapFragment  : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapBinding

    private val usfRepository = UsfRepository()

    private val mapViewModel: MapViewModel by activityViewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) enableMyLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel.adicionarPin.observe(viewLifecycleOwner) { solicitar ->
            if (solicitar == true && ::mMap.isInitialized) {
                adicionarPin()
                mapViewModel.pinAdicionado()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        solicitarPermissaoLocalizacao()
        carregarPinsUsfs()
        configurarCliquePin()
    }

    private fun solicitarPermissaoLocalizacao() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> enableMyLocation()

            else -> locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        mMap.isMyLocationEnabled = true

        val fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13f))
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(-8.0631, -34.8711), 12f
                ))
            }
        }
    }

    private fun carregarPinsUsfs() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val usfs = usfRepository.getUsfs()
                usfs.forEach { usf ->
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(usf.latitude, usf.longitude))
                            .title(usf.nomeOficial)
                            .snippet(usf.endereco)
                    )?.tag = usf
                }
            } catch (e: Exception) {
                Log.e("MapFragment", "Erro ao carregar USFs", e)
                Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun configurarCliquePin() {
        mMap.setOnMarkerClickListener { marker ->
            mostrarOpcoes(marker)
            true
        }
    }

    private fun mostrarOpcoes(marker: Marker) {
        val usf = marker.tag as? Usf ?: return

        val opcoes = arrayOf("👁️ Ver mais", "🗺️ Gerar rota")

        AlertDialog.Builder(requireContext())
            .setTitle(usf.nomeOficial)
            .setItems(opcoes) { _, which ->
                when (which) {
                    0 -> navegarParaPerfil(usf)
                    1 -> gerarRota(usf)
                }
            }
            .show()
    }

    private fun navegarParaPerfil(usf: Usf) {
        val action = MapFragmentDirections.actionNavMapToUsfDetailFragment(usf.id)
        findNavController().navigate(action)
    }

    private fun gerarRota(usf: Usf) {
        val uri = "google.navigation:q=${usf.latitude},${usf.longitude}".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            val uriBrowser =
                "https://maps.google.com/?daddr=${usf.latitude},${usf.longitude}".toUri()
            startActivity(Intent(Intent.ACTION_VIEW, uriBrowser))
        }
    }

    private fun adicionarPin() {
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(-8.0631, -34.8711))
                .title("Recife")
                .snippet("Descrição do local")
        )
    }
}