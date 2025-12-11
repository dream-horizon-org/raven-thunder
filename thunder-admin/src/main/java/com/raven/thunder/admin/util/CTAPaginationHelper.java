package com.raven.thunder.admin.util;

import com.raven.thunder.admin.io.response.StatusWiseCount;
import com.raven.thunder.core.model.CTA;
import com.raven.thunder.core.model.CTAStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** Utility class for pagination and filtering operations on CTA lists. */
public final class CTAPaginationHelper {

  private CTAPaginationHelper() {
    // Utility class - prevent instantiation
  }

  /**
   * Paginates a list of CTAs based on page number and page size.
   *
   * @param ctas the list of CTAs to paginate
   * @param pageNumber the page number (0-indexed)
   * @param pageSize the number of items per page
   * @return the paginated list of CTAs
   */
  public static List<CTA> paginate(List<CTA> ctas, int pageNumber, int pageSize) {
    int ctaSize = ctas.size();
    int maxPages = ctaSize / pageSize;

    if (pageNumber > maxPages || pageNumber < 0) {
      return List.of();
    }

    int startIndex = Math.max(0, Math.min(pageNumber * pageSize, ctaSize - 1));
    int numberOfItems = Math.max(0, Math.min(pageSize, ctaSize - startIndex));

    return ctas.subList(startIndex, startIndex + numberOfItems);
  }

  /**
   * Calculates the total number of pages for a given list size and page size.
   *
   * @param totalEntries the total number of entries
   * @param pageSize the number of items per page
   * @return the total number of pages
   */
  public static int calculateTotalPages(int totalEntries, int pageSize) {
    if (pageSize <= 0) {
      return 0;
    }
    return (totalEntries + pageSize - 1) / pageSize;
  }

  /**
   * Counts CTAs by status and returns a StatusWiseCount object.
   *
   * @param ctas the list of CTAs to count
   * @return StatusWiseCount containing counts for each status
   */
  public static StatusWiseCount countByStatus(List<CTA> ctas) {
    Map<CTAStatus, Integer> statusCounts = new HashMap<>();

    // Initialize all statuses to 0
    for (CTAStatus status : CTAStatus.values()) {
      statusCounts.put(status, 0);
    }

    // Count CTAs by status
    ctas.forEach(cta -> statusCounts.merge(cta.getCtaStatus(), 1, Integer::sum));

    // Convert to AtomicInteger for StatusWiseCount (maintains compatibility)
    return new StatusWiseCount(
        new AtomicInteger(statusCounts.getOrDefault(CTAStatus.DRAFT, 0)),
        new AtomicInteger(statusCounts.getOrDefault(CTAStatus.PAUSED, 0)),
        new AtomicInteger(statusCounts.getOrDefault(CTAStatus.LIVE, 0)),
        new AtomicInteger(statusCounts.getOrDefault(CTAStatus.SCHEDULED, 0)),
        new AtomicInteger(statusCounts.getOrDefault(CTAStatus.CONCLUDED, 0)),
        new AtomicInteger(statusCounts.getOrDefault(CTAStatus.TERMINATED, 0)));
  }

  /**
   * Filters CTAs by status.
   *
   * @param ctas the list of CTAs to filter
   * @param ctaStatus the status string to filter by (case-insensitive)
   * @return the filtered list of CTAs, or the original list if status is invalid
   */
  public static List<CTA> filterByStatus(List<CTA> ctas, String ctaStatus) {
    if (ctaStatus == null || ctaStatus.isEmpty()) {
      return ctas;
    }

    try {
      CTAStatus status = CTAStatus.valueOf(ctaStatus.toUpperCase());
      return ctas.stream()
          .filter(cta -> cta.getCtaStatus().equals(status))
          .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
      return ctas;
    }
  }
}
