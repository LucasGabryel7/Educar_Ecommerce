
package br.unitins.util;

import br.unitins.repository.AgendamentoAulaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TestCleanup {

    @Inject
    AgendamentoAulaRepository agendamentoRepository;

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbcUrl;

    @Transactional
    public void limparAgendamentos() {

        // Identifica o banco usado pela conexão atual.
        String banco = jdbcUrl.substring(jdbcUrl.lastIndexOf('/') + 1);

        // Remove parâmetros adicionais da URL, se existirem.
        int indiceParametros = banco.indexOf('?');
        if (indiceParametros >= 0) {
            banco = banco.substring(0, indiceParametros);
        }

        // Impede a limpeza se não estivermos no banco de testes.
        if (!"autoescola_test".equals(banco)) {
            throw new IllegalStateException(
                    "Limpeza bloqueada: o banco conectado não é autoescola_test.");
        }

        agendamentoRepository.deleteAll();
    }
}