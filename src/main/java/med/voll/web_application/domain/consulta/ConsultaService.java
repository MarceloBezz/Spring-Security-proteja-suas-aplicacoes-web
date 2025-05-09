package med.voll.web_application.domain.consulta;

import jakarta.transaction.Transactional;
import med.voll.web_application.domain.RegraDeNegocioException;
import med.voll.web_application.domain.medico.MedicoRepository;
import med.voll.web_application.domain.paciente.PacienteRepository;
import med.voll.web_application.domain.usuario.Perfil;
import med.voll.web_application.domain.usuario.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class ConsultaService {

    private final ConsultaRepository repository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaService(ConsultaRepository repository, MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository) {
        this.repository = repository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public Page<DadosListagemConsulta> listar(Pageable paginacao, Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == Perfil.ATENDENTE) {
            return repository.findAllByOrderByData(paginacao).map(DadosListagemConsulta::new);
        }

        return repository
                .buscaPersonalizadaConsultas(usuarioLogado.getId(), paginacao)
                .map(DadosListagemConsulta::new);
    }

    @Transactional
    public void cadastrar(DadosAgendamentoConsulta dados, Usuario usuarioLogado) {
        var medicoConsulta = medicoRepository.findById(dados.idMedico()).orElseThrow();
        var pacienteConsulta = pacienteRepository.findByCpf(dados.paciente()).orElseThrow();

        if (usuarioLogado.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PACIENTE"))
                && !pacienteConsulta.getId().equals(usuarioLogado.getId())) {
            throw new RegraDeNegocioException("CPF inválido!");
        }

        if (dados.id() == null) {
            repository.save(new Consulta(medicoConsulta, pacienteConsulta, dados));
        } else {
            var consulta = repository.findById(dados.id()).orElseThrow();
            consulta.modificarDados(medicoConsulta, pacienteConsulta, dados);
        }
    }

    @PreAuthorize("hasRole('ATENDENTE') or " +
            "(hasRole('PACIENTE') and @consultaRepository.findById(#id).get().paciente.id == principal.id)")
    public DadosAgendamentoConsulta carregarPorId(Long id) {
        var consulta = repository.findById(id).orElseThrow();
        return new DadosAgendamentoConsulta(consulta.getId(), consulta.getMedico().getId(),
                consulta.getPaciente().getNome(), consulta.getData(), consulta.getMedico().getEspecialidade());
    }

    @Transactional
    @PreAuthorize("hasRole('ATENDENTE') or " +
            "(hasRole('PACIENTE') and @consultaRepository.findById(#id).get().paciente.id == principal.id) or " +
            "(hasRole('MEDICO') and @consultaRepository.findById(#id).get().medico.id == principal.id)")
    public void excluir(Long id) {
        repository.deleteById(id);
    }

}