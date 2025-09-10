export const useAnalytics = () => {
  const trackEvent = (eventName, eventData = {}) => {
    if (typeof window !== 'undefined' && window.gtag) {
      window.gtag('event', eventName, eventData);
    }
    
    // También puedes enviar a tu propio backend
    console.log('Event tracked:', eventName, eventData);
  };

  const trackPageView = (pagePath) => {
    if (typeof window !== 'undefined' && window.gtag) {
      window.gtag('config', process.env.VUE_APP_GA_MEASUREMENT_ID, {
        page_path: pagePath,
      });
    }
  };

  const trackConversion = (conversionData) => {
    if (typeof window !== 'undefined' && window.gtag) {
      window.gtag('event', 'conversion', conversionData);
    }
  };

  const setUserProperties = (userId, userProperties) => {
    if (typeof window !== 'undefined' && window.gtag) {
      window.gtag('set', 'user_properties', userProperties);
    }
  };

  return {
    trackEvent,
    trackPageView,
    trackConversion,
    setUserProperties
  };
};

export default useAnalytics;