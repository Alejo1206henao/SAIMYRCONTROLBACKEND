package co.com.saimyr.persistence;

import co.com.saimyr.domain.SmrAdmBill;
import co.com.saimyr.domain.repository.SaimyrBillRepository;
import co.com.saimyr.persistence.crud.FacturaCrudRepository;
import co.com.saimyr.persistence.entity.SaimyrAdmFactura;
import co.com.saimyr.persistence.mapper.SmrAdmBillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class SaimyrAdmFacImple implements SaimyrBillRepository {

    private final FacturaCrudRepository facturaCrudRepository;
    private final SmrAdmBillMapper mapper;

    @Autowired
    public SaimyrAdmFacImple(FacturaCrudRepository facturaCrudRepository, SmrAdmBillMapper mapper) {
        this.facturaCrudRepository = facturaCrudRepository;
        this.mapper = mapper;
    }

    @Override
    public List<SmrAdmBill> getAll() {
        List<SaimyrAdmFactura> saimyrAdmFacturas = (List<SaimyrAdmFactura>) facturaCrudRepository.findAll();
        return mapper.toSmrAdmBills(saimyrAdmFacturas);
    }

    @Override
    public List<SmrAdmBill> getById(String smrNumber) {
        List<SaimyrAdmFactura> saimyrAdmFacturas = (List<SaimyrAdmFactura>) facturaCrudRepository.findBySmrNumero(smrNumber);
        return mapper.toSmrAdmBills(saimyrAdmFacturas);
    }

    @Override
    public List<SmrAdmBill> getByDate(LocalDate date) {
        List<SaimyrAdmFactura> saimyrAdmFacturas = (List<SaimyrAdmFactura>) facturaCrudRepository.findByFecha(date);
        return mapper.toSmrAdmBills(saimyrAdmFacturas);
    }

    @Override
    public Optional<List<SmrAdmBill>> getByStatu(String statu) {
        Optional<List<SaimyrAdmFactura>> saimyrAdmFacturas = facturaCrudRepository.findByEstado(statu);
        return Optional.empty();
    }

    @Override
    public List<SmrAdmBill> save(List<SmrAdmBill> smrAdmBills) {
        this.setNextConsecutives(smrAdmBills);
        List<SaimyrAdmFactura> saimyrAdmFacturas = mapper.toSaimyrAdmFacturaList(smrAdmBills);
        return mapper.toSmrAdmBills(facturaCrudRepository.saveAll(saimyrAdmFacturas));
    }

    private void setNextConsecutives(List<SmrAdmBill> smrAdmBillList) {
        AtomicInteger maxiBillFac = new AtomicInteger((int) facturaCrudRepository.getMaxControlId());
        smrAdmBillList.forEach(detail -> {
            if (detail.getConsBill() == 0) {
                detail.setConsBill(maxiBillFac.getAndIncrement());
            }
        });
    }
}
