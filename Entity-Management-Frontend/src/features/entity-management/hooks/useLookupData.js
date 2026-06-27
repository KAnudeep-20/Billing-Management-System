import { useState, useEffect } from 'react';
import entityService from '../../../services/entityService';

export default function useLookupData() {
  const [lookups, setLookups] = useState({
    entityTypes: [],
    relationshipTypes: [],
    siteUses: [],
    paymentTerms: [],
    contactTypes: [],
    billingCycles: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let active = true;

    async function fetchAllLookups() {
      try {
        setLoading(true);
        // Call all lookup APIs in parallel
        const [
          entityTypesRes,
          relationshipTypesRes,
          siteUsesRes,
          paymentTermsRes,
          contactTypesRes,
          billingCyclesRes,
        ] = await Promise.all([
          entityService.getEntityTypeLookup(),
          entityService.getRelationshipTypeLookup(),
          entityService.getSiteUseLookup(),
          entityService.getPaymentTermLookup(),
          entityService.getContactTypeLookup(),
          entityService.getBillingCycleLookup(),
        ]);

        if (active) {
          // Spring Boot responses wrap the payload in an 'ApiResponse' where the actual lookup data is under 'data'
          setLookups({
            entityTypes: entityTypesRes.data || [],
            relationshipTypes: relationshipTypesRes.data || [],
            siteUses: siteUsesRes.data || [],
            paymentTerms: paymentTermsRes.data || [],
            contactTypes: contactTypesRes.data || [],
            billingCycles: billingCyclesRes.data || [],
          });
          setLoading(false);
        }
      } catch (err) {
        if (active) {
          console.error('Failed to load lookup data:', err);
          setError(err.message || 'Failed to load lookup master data.');
          setLoading(false);
        }
      }
    }

    fetchAllLookups();

    return () => {
      active = false;
    };
  }, []);

  return { lookups, loading, error };
}
