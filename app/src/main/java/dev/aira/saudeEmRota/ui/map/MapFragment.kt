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
import android.widget.EditText
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapFragment  : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentMapBinding

    private val usfRepository = UsfRepository()

    private var listaUsfs: List<Usf> = emptyList()

    private var modoCriacao = false
    private var markerTemp: Marker? = null

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

        mapViewModel.busca.observe(viewLifecycleOwner) { query ->
            buscarEIrParaUsf(query)
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
                listaUsfs = usfRepository.getUsfs()

                listaUsfs.forEach { usf ->
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(usf.latitude, usf.longitude))
                            .title(usf.nomeOficial)
                            .snippet(usf.endereco)
                    )?.tag = usf
                }

            } catch (e: Exception) {
                Log.e("MapFragment", "Erro ao carregar USFs", e)
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

    private fun buscarEIrParaUsf(query: String) {
        val usfEncontrada = listaUsfs.find {
            it.nomeOficial.contains(query, ignoreCase = true)
                    || it.id.contains(query, ignoreCase = true)
        }

        if (usfEncontrada != null) {
            val latLng = LatLng(usfEncontrada.latitude, usfEncontrada.longitude)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(usfEncontrada.nomeOficial)
            )
            marker?.tag = usfEncontrada

            marker?.showInfoWindow()
            marker?.let { mostrarOpcoes(it) }

        } else {
            Toast.makeText(requireContext(), "USF não encontrada", Toast.LENGTH_SHORT).show()
        }
    }

    fun ativarModoCriacao() {
        modoCriacao = true

        val centro = mMap.cameraPosition.target

        markerTemp?.remove()

        markerTemp = mMap.addMarker(
            MarkerOptions()
                .position(centro)
                .title("Arraste para definir local")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )

        Toast.makeText(requireContext(),
            "Arraste o pin para o local desejado", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogCriarUsf(latLng: LatLng) {
        val input = EditText(requireContext()).apply {
            hint = "Nome da unidade"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Nova Unidade de Saúde")
            .setMessage("Digite o nome da unidade:")
            .setView(input)
            .setPositiveButton("Criar") { _, _ ->
                val nome = input.text.toString().trim()

                if (nome.isNotEmpty()) {
                    criarUsfUsuario(nome, latLng)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun criarUsfUsuario(nome: String, latLng: LatLng) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val usf = Usf(
                    nomeOficial = nome,
                    numeroUS = "USER_${System.currentTimeMillis()}",
                    distrito = 0,
                    cnes = "",
                    endereco = "Não informado",
                    bairro = "Não informado",
                    fone = "",
                    especialidade = "",
                    horario = "",
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    ativa = true,
                    chaveRelacao = nome.lowercase(),

                    criadaPorUsuario = true,
                    usuarioId = "user_mock",
                    aprovada = false,
                    criadaEm = System.currentTimeMillis()
                )
                val idGerado = usfRepository.criarUsf(usf)
                val usfComId = usf.copy(id = idGerado)
                adicionarPin(usfComId)

            } catch (e: Exception) {
                Toast.makeText(requireContext(),
                    "Erro ao criar unidade", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adicionarPin(usf: Usf) {
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(usf.latitude, usf.longitude))
                .title(usf.nomeOficial)
                .snippet("Adicionado por usuário")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
    }
}