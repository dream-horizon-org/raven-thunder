import React from 'react';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';

export default function Home() {
  return (
    <Layout
      title="Raven Thunder"
      description="The rules, journeys, and intelligence layer of Raven's engagement system">
      <div className="hero hero-fullwidth" style={{
        padding: '4rem 0',
        width: '100%',
        margin: 0,
        textAlign: 'center'
      }}>
        <div className="container" style={{
          maxWidth: '1200px',
          margin: '0 auto',
          padding: '0 2rem'
        }}>
          <h1 className="hero__title" style={{
            fontWeight: 700,
            fontSize: '3rem',
            letterSpacing: '-0.02em',
            textAlign: 'center',
            marginBottom: '1rem'
          }}>
            Raven Thunder
          </h1>
          <p className="hero__subtitle" style={{
            fontWeight: 400,
            fontSize: '1.25rem',
            maxWidth: '700px',
            margin: '0 auto',
            textAlign: 'center',
            lineHeight: 1.6,
            marginBottom: '2rem'
          }}>
            The rules, journeys, and intelligence layer of{' '}
            <Link
              href="https://raven.dreamhorizon.org/"
              style={{
                color: 'var(--ifm-color-primary)',
                fontWeight: 600,
                textDecoration: 'none'
              }}
              onMouseEnter={(e) => {
                e.target.style.textDecoration = 'underline';
              }}
              onMouseLeave={(e) => {
                e.target.style.textDecoration = 'none';
              }}>
              Raven's
            </Link>
            {' '}engagement system
          </p>
          <div className="hero__buttons" style={{
            display: 'flex',
            gap: '1rem',
            justifyContent: 'center',
            flexWrap: 'wrap',
            marginTop: '2rem'
          }}>
            <Link
              className="button button--primary button--lg"
              to="/raven-thunder/getting-started/overview">
              Get Started
            </Link>
            <Link
              className="button button--secondary button--lg"
              href="https://github.com/dream-horizon-org/thunder">
              View on GitHub
            </Link>
          </div>
        </div>
      </div>

      <div className="container" style={{
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '3rem',
        textAlign: 'center'
      }}>
        <h2 className="platform-components-heading" style={{
          textAlign: 'center',
          margin: '3rem 0 2rem 0',
          fontWeight: 600,
          fontSize: '1.75rem'
        }}>Platform Components</h2>

        <div className="row" style={{
          display: 'flex',
          flexWrap: 'wrap',
          margin: '0 -0.5rem',
          justifyContent: 'center',
          width: '100%',
          marginBottom: '4rem'
        }}>
          {[
            {
              emoji: '📊',
              title: 'Raven Panel',
              description: 'The control panel for orchestrating Raven\'s customer engagement platform',
              link: 'https://dream-horizon-org.github.io/raven-panel/'
            },
            {
              emoji: '📱',
              title: 'Raven Client',
              description: 'The in-app delivery layer of Raven\'s customer engagement platform',
              link: 'https://dream-horizon-org.github.io/raven-client/'
            }
          ].map((component, idx) => (
            <div key={idx} className="col col--6" style={{
              padding: '0 0.5rem',
              marginBottom: '2rem',
              flex: '0 0 50%',
              maxWidth: '50%',
              minWidth: '300px'
            }}>
              <Link
                href={component.link}
                style={{
                  textDecoration: 'none',
                  color: 'inherit',
                  display: 'block'
                }}>
                <div className="card platform-component-card" style={{
                  borderRadius: '8px',
                  boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)',
                  height: '100%',
                  transition: 'all 0.2s ease',
                  textAlign: 'center',
                  cursor: 'pointer',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'center',
                  padding: '2rem 1.5rem'
                }}>
                  <div className="card__header" style={{
                    padding: '0 0 1rem 0',
                    textAlign: 'center'
                  }}>
                    <h3 style={{
                      marginBottom: 0,
                      fontSize: '1.5rem',
                      fontWeight: 600,
                      textAlign: 'center'
                    }}>
                      {component.emoji} {component.title}
                    </h3>
                  </div>
                  <div className="card__body" style={{
                    padding: 0,
                    textAlign: 'center'
                  }}>
                    <p style={{
                      margin: 0,
                      fontSize: '1rem',
                      lineHeight: '1.6',
                      textAlign: 'center'
                    }}>{component.description}</p>
                  </div>
                </div>
              </Link>
            </div>
          ))}
        </div>

        <h2 className="features-heading" style={{
          textAlign: 'center',
          margin: '3rem 0 2rem 0',
          fontWeight: 600,
          fontSize: '1.75rem'
        }}>Features</h2>

        <div className="row" style={{
          display: 'flex',
          flexWrap: 'wrap',
          margin: '0 -0.5rem',
          justifyContent: 'center',
          width: '100%'
        }}>
          {[
            {
              emoji: '🚀',
              title: 'Multi-Module Architecture',
              description: 'Clean separation with thunder-core, thunder-api, and thunder-admin modules.'
            },
            {
              emoji: '🔌',
              title: 'REST APIs',
              description: 'Complete Admin API (19 endpoints) and SDK/Debug APIs (7 endpoints) for managing CTA Journeys and Behaviour.'
            },
            {
              emoji: '💾',
              title: 'Aerospike Integration',
              description: 'Reactive data access with RxJava3, complete with Docker setup, seed data, and indexes for local development.'
            },
            {
              emoji: '🐳',
              title: 'Docker Ready',
              description: 'Full Docker Compose setup with Aerospike, automatic seed data, and pre-configured indexes.'
            },
            {
              emoji: '✅',
              title: 'Health Checks',
              description: 'Comprehensive health monitoring for services and Aerospike connectivity.'
            },
            {
              emoji: '🧪',
              title: 'Testing',
              description: 'CI with unit and integration tests, release as Docker images and fat JARs.'
            }
          ].map((feature, idx) => (
            <div key={idx} className="col col--4" style={{
              padding: '0 0.5rem',
              marginBottom: '2rem',
              flex: '0 0 33.333333%',
              maxWidth: '33.333333%',
              minWidth: '280px'
            }}>
              <div className="card feature-card" style={{
                borderRadius: '8px',
                boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)',
                height: '100%',
                transition: 'all 0.2s ease'
              }}>
                <div className="card__header" style={{
                  padding: '1.25rem 1.25rem 0.75rem 1.25rem'
                }}>
                  <h3 style={{
                    marginBottom: 0,
                    fontSize: '1.25rem',
                    fontWeight: 600
                  }}>
                    {feature.emoji} {feature.title}
                  </h3>
                </div>
                <div className="card__body" style={{
                  padding: '0 1.25rem 1.25rem 1.25rem'
                }}>
                  <p>{feature.description}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  );
}

