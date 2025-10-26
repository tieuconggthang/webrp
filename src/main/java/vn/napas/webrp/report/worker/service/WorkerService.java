package vn.napas.webrp.report.worker.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.napas.webrp.database.converter.IbftMapper;
import vn.napas.webrp.database.entities.*;
import vn.napas.webrp.database.repo.*;
@Service
@RequiredArgsConstructor
public class WorkerService {
//  private final IsoMessageTmpTurnRepo isoRepo;
//  private final ShclogSettIbftRepository shcRepo;
//  private final IbftMapperFactory mapper; // tạo mapper/converters
	private final ShclogSettIbftRepo scIbftRepo;

////  @Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void processPage(List<IsomessageTmpTurn> rows) {
//		// 1) SELECT theo range (keyset) – có thể bật TiFlash/MPP ở session nếu cần
////    List<IsomessageTmpTurn> rows = isoRepo.fetchRange(spec.startExclusive(), spec.endInclusive());
////    if (rows.isEmpty()) return new PageResult(0);
//
//		// 2) Convert sang entity đích
////    List<shclog> batch = rows.stream()
////        .map(mapper::toShclogSett) // dùng createShcLogSettIfgtFromIsomessageTmpTurn()
////        .toList();
////
////    // 3) Batch insert: dùng BaseBatchRepository.insertBatch(batch)
////    shcRepo.insertBatch(batch); // 1 flush/clear cho cả batch
//		
//		for (IsomessageTmpTurn ele : rows) {
//			ShclogSettIbft shclogSettIbft = IbftMapper.createShcLogSettIfgtFromIsomessageTmpTurn(ele, null, null, null, null, null);
//			
//		}
////    return new PageResult(batch.size());
//	}
}
