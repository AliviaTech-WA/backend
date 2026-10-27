package com.upc.backend.serviceimpl;
import com.upc.backend.dtos.ActualizarPsicologoPerfilRequestDTO;
import com.upc.backend.dtos.PsicologoPerfilDTO;
import com.upc.backend.entities.Psicologo;
import com.upc.backend.entities.Usuario;
import com.upc.backend.exceptions.ConflictException;
import com.upc.backend.exceptions.InvalidRequestException;
import com.upc.backend.exceptions.ResourceNotFoundException;
import com.upc.backend.repositories.PsicologoRepositorio;
import com.upc.backend.repositories.UsuarioRepositorio;
import com.upc.backend.services.PsicologoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PsicologoServiceImpl implements PsicologoService {
    private final UsuarioRepositorio usuarioRepositorio;
    private final PsicologoRepositorio psicologoRepositorio;
    private final ModelMapper modelMapper;

    public PsicologoServiceImpl(
            UsuarioRepositorio usuarioRepositorio,
            PsicologoRepositorio psicologoRepositorio,
            ModelMapper modelMapper) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.psicologoRepositorio = psicologoRepositorio;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    // HU34 | END31: carga y convierte el perfil profesional solicitado.
    public PsicologoPerfilDTO buscarPerfilPorId(Long psicologoId) {
        return convertirPerfil(buscarPsicologo(psicologoId));
    }

    @Override
    @Transactional
    // HU34 | END32: modifica solo correo, teléfono y especialidad cuando fueron enviados.
    public PsicologoPerfilDTO actualizarPerfil(Long psicologoId, ActualizarPsicologoPerfilRequestDTO request) {
        validarCamposActualizacion(request);
        Psicologo psicologo = buscarPsicologo(psicologoId);
        Usuario usuario = psicologo.getUsuario();

        if (request.getCorreo() != null) {
            validarDuplicidad(null, request.getCorreo(), usuario.getId());
            usuario.setCorreo(request.getCorreo().trim());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono().trim());
        }
        if (request.getEspecialidad() != null) {
            psicologo.setEspecialidad(request.getEspecialidad().trim());
        }

        usuarioRepositorio.save(usuario);
        psicologoRepositorio.save(psicologo);
        return convertirPerfil(psicologo);
    }

    // HU34: centraliza la búsqueda del perfil y la respuesta 404 cuando no existe.
    private Psicologo buscarPsicologo(Long psicologoId) {
        return psicologoRepositorio.findById(psicologoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el psicólogo con ID " + psicologoId + "."));
    }

    // HU01 y HU34: combina los datos de Usuario y Psicologo en el DTO de perfil.
    private PsicologoPerfilDTO convertirPerfil(Psicologo psicologo) {
        PsicologoPerfilDTO response = modelMapper.map(psicologo.getUsuario(), PsicologoPerfilDTO.class);
        response.setPsicologoId(psicologo.getId());
        response.setNumeroColegiatura(psicologo.getNumeroColegiatura());
        response.setEspecialidad(psicologo.getEspecialidad());
        return response;
    }

    // HU01 y HU34: evita DNI o correo duplicados, excluyendo el usuario actual al editar.
    private void validarDuplicidad(String dni, String correo, Long usuarioExcluidoId) {
        boolean dniDuplicado = dni != null && usuarioRepositorio.existsByDni(dni);
        boolean correoDuplicado = correo != null && usuarioRepositorio.findByCorreoIgnoreCase(correo)
                .filter(usuario -> usuarioExcluidoId == null || !usuario.getId().equals(usuarioExcluidoId))
                .isPresent();
        boolean duplicado = dniDuplicado || correoDuplicado;
        if (duplicado) {
            throw new ConflictException("El DNI o correo ya está asociado a otra cuenta.");
        }
    }

    // HU34: exige al menos un cambio y rechaza valores vacíos.
    private void validarCamposActualizacion(ActualizarPsicologoPerfilRequestDTO request) {
        if (request.getCorreo() == null && request.getTelefono() == null && request.getEspecialidad() == null) {
            throw new InvalidRequestException("Debe enviar al menos un campo para actualizar.");
        }
        validarNoVacio(request.getCorreo(), "correo");
        validarNoVacio(request.getTelefono(), "telefono");
        validarNoVacio(request.getEspecialidad(), "especialidad");
    }
    // HU34: valida campos opcionales únicamente cuando están presentes.
    private void validarNoVacio(String valor, String campo) {
        if (valor != null && valor.isBlank()) {
            throw new InvalidRequestException("El campo " + campo + " no puede estar vacío.");
        }
    }
}
